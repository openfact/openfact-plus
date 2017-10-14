package org.openfact.services.managers;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.openfact.documents.DocumentModel;
import org.openfact.documents.DocumentProvider;
import org.openfact.documents.exceptions.UnreadableDocumentException;
import org.openfact.documents.exceptions.UnsupportedDocumentTypeException;
import org.openfact.files.*;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.files.exceptions.FileStorageException;
import org.openfact.models.exceptions.ModelException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

@Stateless
public class DocumentManager {

    private static final Logger logger = Logger.getLogger(DocumentManager.class);

    @Inject
    private FileProvider fileProvider;

    @Inject
    private DocumentProvider documentProvider;

    /**
     * @param bytes
     * @throws UnreadableDocumentException   in case InputStream passed could not be processed
     * @throws FileStorageException in case could not be persist file on storage
     * @throws ModelException        in case unexpected error happens
     */
    public DocumentModel importDocument(byte[] bytes) throws FileStorageException, UnsupportedDocumentTypeException, FileFetchException, UnreadableDocumentException {
        FileModel fileModel = fileProvider.addFile(bytes, ".xml");

        try {
            FlyWeightXmlUBLFileModel flyWeightFile = new FlyWeightXmlUBLFileModel(
                    new FlyWeightXmlFileModel(
                            new FlyWeightFileModel(fileModel))
            );
            return documentProvider.addDocument(flyWeightFile);
        } catch (UnsupportedDocumentTypeException | UnreadableDocumentException e) {
            boolean result = fileProvider.removeFile(fileModel);
            logger.debug("Rollback file result=" + result);
            throw e;
        }
    }

    /**
     * @param inputStream java.io.InputStream
     * @throws UnreadableDocumentException   in case InputStream passed could not be processed
     * @throws FileStorageException in case could not be persist file on storage
     * @throws ModelException        in case unexpected error happens
     */
    public DocumentModel importDocument(InputStream inputStream) throws IOException, FileStorageException, UnsupportedDocumentTypeException, FileFetchException, UnreadableDocumentException {
        return importDocument(IOUtils.toByteArray(inputStream));
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
