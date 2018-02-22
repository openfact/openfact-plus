package org.clarksnut.managers;

import org.apache.commons.io.IOUtils;
import org.clarksnut.models.DocumentProviderType;
import org.clarksnut.models.ImportedDocumentModel;
import org.clarksnut.models.ImportedDocumentProvider;
import org.clarksnut.models.ImportedDocumentStatus;
import org.clarksnut.files.BasicFileModel;
import org.clarksnut.files.FileModel;
import org.clarksnut.files.FileModelUtils;
import org.clarksnut.files.FileProvider;
import org.clarksnut.files.uncompress.exceptions.NotReadableCompressFileException;
import org.clarksnut.models.exceptions.*;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ImportedDocumentManager {

    private static final Logger logger = Logger.getLogger(ImportedDocumentManager.class);

    @Inject
    private FileProvider fileProvider;

    @Inject
    private ImportedDocumentProvider importedDocumentProvider;

    @Inject
    private DocumentManager documentManager;

    public void importDocument(byte[] bytes, String filename, DocumentProviderType providerType)
            throws IsNotXmlOrCompressedFileDocumentException, NotReadableCompressFileException {
        if (!FileModelUtils.isXmlOrCompressedFilename(filename)) {
            throw new IsNotXmlOrCompressedFileDocumentException("Could not read files that are not xml or compressed");
        }

        List<FileModel> filesToImport;
        if (!FileModelUtils.isCompressedFilename(filename)) {
            FileModel file = fileProvider.addFile(filename, bytes);
            filesToImport = Collections.singletonList(file);
        } else {
            BasicFileModel file = new BasicFileModel(filename, bytes);
            filesToImport = FileModelUtils.uncompress(file).stream()
                    .map(f -> fileProvider.addFile(f.getFilename(), f.getFile()))
                    .collect(Collectors.toList());
        }

        List<ImportedDocumentModel> importedDocuments = filesToImport.stream()
                .map(f -> importedDocumentProvider.importDocument(f, providerType))
                .collect(Collectors.toList());
        for (ImportedDocumentModel importedDocument : importedDocuments) {
            try {
                documentManager.addDocument(importedDocument);
                importedDocument.setStatus(ImportedDocumentStatus.IMPORTED);
            } catch (UnsupportedDocumentTypeException e) {
                logger.warn("Unsupported Document Type, Keeping it for future versions");
            } catch (AlreadyImportedDocumentException e) {
                logger.warn("Already imported document, clearing imported document and attached files");
                importedDocumentProvider.removeImportedDocument(importedDocument);
            } catch (IsNotUBLDocumentException e) {
                logger.error("Document is not a valid UBL Xml File, clearing imported document and attached files");
                importedDocumentProvider.removeImportedDocument(importedDocument);
            } catch (ImpossibleToUnmarshallException e) {
                logger.warn("Impossible to map Document, Keeping it for future versions");
            }
        }
    }

    public void importDocument(InputStream inputStream, String filename, DocumentProviderType providerType)
            throws IOException, IsNotXmlOrCompressedFileDocumentException, NotReadableCompressFileException {
        importDocument(IOUtils.toByteArray(inputStream), filename, providerType);
    }
}
