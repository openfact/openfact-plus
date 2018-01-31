package org.clarksnut.managers;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.clarksnut.documents.*;
import org.clarksnut.documents.exceptions.UnreadableDocumentException;
import org.clarksnut.documents.exceptions.UnsupportedDocumentTypeException;
import org.clarksnut.files.*;
import org.clarksnut.files.uncompress.UncompressFileProviderFacade;
import org.clarksnut.files.uncompress.UncompressedFileModel;
import org.clarksnut.files.uncompress.exceptions.NotReadableCompressFileException;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class ImportedDocumentManager {

    private static final Logger logger = Logger.getLogger(ImportedDocumentManager.class);

    @Inject
    private FileProvider fileProvider;

    @Inject
    private ImportedDocumentProvider importedDocumentProvider;

    @Inject
    private DocumentProvider documentProvider;

    private DocumentModel addDocument(ImportedDocumentModel importedDocument) throws
            UnsupportedDocumentTypeException,
            UnreadableDocumentException {
        XmlUBLFileModel xmlUBLFile = new FlyWeightXmlUBLFileModel(
                new FlyWeightXmlFileModel(
                        new FlyWeightFileModel(importedDocument.getFile()))
        );
        return documentProvider.addDocument(importedDocument, xmlUBLFile);
    }

    public void importDocument(byte[] bytes, String filename, DocumentProviderType providerType) throws NotReadableCompressFileException {
        FileModel file = fileProvider.addFile(filename, bytes);

        CompressedFileModel fileToImport;

        // Uncompress if need
        UncompressedFileModel uncompressedFile = new UncompressFileProviderFacade().uncompress(filename, bytes);
        if (!uncompressedFile.isCompressedFile()) {
            fileToImport = new BasicCompressedFileModel(file);
        } else {
            List<FileModel> childrenFiles = uncompressedFile.getEntries()
                    .stream()
                    .map(fileEntry -> fileProvider.addFile(fileEntry.getFilename(), fileEntry.getBytes()))
                    .collect(Collectors.toList());

            fileToImport = new BasicCompressedFileModel(file, childrenFiles);
        }

        // Import uncompressed files
        ImportedDocumentModel importedDocument = importedDocumentProvider.importDocument(fileToImport, providerType);

        Set<ImportedDocumentModel> documentsToImport;
        if (!importedDocument.isCompressed()) {
            documentsToImport = new HashSet<>(Collections.singletonList(importedDocument));
        } else {
            documentsToImport = importedDocument.getChildren();
        }

        documentsToImport.stream()
                .filter(p -> {
                    String filenameExtension = FilenameUtils.getExtension(p.getFile().getFilename());
                    return filenameExtension.equalsIgnoreCase("xml");
                })
                .forEach(item -> {
                    try {
                        DocumentModel document = addDocument(item);
                        item.setStatus(ImportedDocumentStatus.IMPORTED);
                        logger.debug("Document created! id:" + document.getId());
                    } catch (UnsupportedDocumentTypeException e) {
                        item.setStatus(ImportedDocumentStatus.NOT_IMPORTED_DUE_TO_UNSUPPORTED_DOCUMENT_TYPE);
                    } catch (UnreadableDocumentException e) {
                        item.setStatus(ImportedDocumentStatus.NOT_IMPORTED_DUE_TO_SUPPORTED_BUT_UNREADABLE_DOCUMENT);
                    }
                });
    }

    public void importDocument(InputStream inputStream, String filename, DocumentProviderType providerType) throws IOException, NotReadableCompressFileException {
        importDocument(IOUtils.toByteArray(inputStream), filename, providerType);
    }
}
