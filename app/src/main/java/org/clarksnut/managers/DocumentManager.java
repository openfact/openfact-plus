package org.clarksnut.managers;

import org.apache.commons.io.IOUtils;
import org.clarksnut.documents.*;
import org.clarksnut.documents.exceptions.UnreadableDocumentException;
import org.clarksnut.documents.exceptions.UnrecognizableDocumentTypeException;
import org.clarksnut.documents.exceptions.UnsupportedDocumentTypeException;
import org.clarksnut.files.*;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.files.exceptions.FileStorageException;
import org.clarksnut.managers.exceptions.DocumentNotImportedButSavedForFutureException;
import org.clarksnut.managers.exceptions.DocumentNotImportedException;
import org.clarksnut.models.exceptions.ModelException;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

@Stateless
public class DocumentManager {

    private static final Logger logger = Logger.getLogger(DocumentManager.class);

    @Inject
    @FileProviderName
    private String fileProviderName;

    @Inject
    private FileProvider fileProvider;

    @Inject
    private DocumentProvider documentProvider;

    @Inject
    private UnsavedDocumentProvider unsavedDocumentProvider;

    /**
     * @param bytes
     * @throws UnreadableDocumentException in case InputStream passed could not be processed
     * @throws FileStorageException        in case could not be persist file on storage
     * @throws ModelException              in case unexpected error happens
     */
    public DocumentModel importDocument(byte[] bytes, DocumentProviderType providerType) throws
            FileStorageException,
            FileFetchException,
            DocumentNotImportedException,
            DocumentNotImportedButSavedForFutureException {
        FileModel fileModel = fileProvider.addFile(bytes, ".xml");

        XmlUBLFileModel xmlUBLFile = new FlyWeightXmlUBLFileModel(
                new FlyWeightXmlFileModel(
                        new FlyWeightFileModel(fileModel))
        );

        try {
            return documentProvider.addDocument(xmlUBLFile, fileProviderName, false, providerType);
        } catch (UnsupportedDocumentTypeException e) {
            unsavedDocumentProvider.addDocument(xmlUBLFile, fileProviderName, UnsavedReasonType.UNSUPPORTED);
            throw new DocumentNotImportedButSavedForFutureException("Document is a valid UBL but does not have a reader", e);
        } catch (UnreadableDocumentException e) {
            unsavedDocumentProvider.addDocument(xmlUBLFile, fileProviderName, UnsavedReasonType.UNREADABLE);
            throw new DocumentNotImportedButSavedForFutureException("Document is a valid UBL but readers could not read it", e);
        } catch (UnrecognizableDocumentTypeException e) {
            throw new DocumentNotImportedException("Document not imported", e);
        }
    }

    /**
     * @param inputStream java.io.InputStream
     * @throws UnreadableDocumentException in case InputStream passed could not be processed
     * @throws FileStorageException        in case could not be persist file on storage
     * @throws ModelException              in case unexpected error happens
     */
    public DocumentModel importDocument(InputStream inputStream, DocumentProviderType providerType)
            throws IOException,
            FileStorageException,
            FileFetchException,
            DocumentNotImportedException,
            DocumentNotImportedButSavedForFutureException {
        return importDocument(IOUtils.toByteArray(inputStream), providerType);
    }

    public boolean removeDocument(DocumentModel ublDocument) {
        FileModel file = fileProvider.getFile(ublDocument.getFileId());

        // Delete document
        boolean result = documentProvider.removeDocument(ublDocument);
        if (!result) {
            return false;
        }

        // Delete file
        try {
            fileProvider.removeFile(file);
        } catch (FileStorageException e) {
            try {
                fileProvider.removeFile(file);
            } catch (FileStorageException e1) {
                logger.error("Could not remove file:" + file.getId());
                logger.warn("Transaction was not rolled back");
            }
        }

        return true;
    }

}
