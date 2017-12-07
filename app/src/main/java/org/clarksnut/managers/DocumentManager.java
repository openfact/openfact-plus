package org.clarksnut.managers;

import org.apache.commons.io.IOUtils;
import org.clarksnut.documents.DocumentProvider;
import org.clarksnut.documents.exceptions.UnreadableDocumentException;
import org.clarksnut.files.*;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.files.exceptions.FileStorageException;
import org.clarksnut.models.exceptions.ModelException;
import org.jboss.logging.Logger;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentProviderType;
import org.clarksnut.documents.exceptions.PreexistedDocumentException;
import org.clarksnut.documents.exceptions.UnsupportedDocumentTypeException;

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
     * @throws UnreadableDocumentException in case InputStream passed could not be processed
     * @throws FileStorageException        in case could not be persist file on storage
     * @throws ModelException              in case unexpected error happens
     */
    public DocumentModel importDocument(byte[] bytes, DocumentProviderType providerType) throws
            FileStorageException,
            FileFetchException,
            UnsupportedDocumentTypeException,
            UnreadableDocumentException,
            PreexistedDocumentException {
        FileModel fileModel = fileProvider.addFile(bytes, ".xml");

        try {
            FlyWeightXmlUBLFileModel flyWeightFile = new FlyWeightXmlUBLFileModel(
                    new FlyWeightXmlFileModel(
                            new FlyWeightFileModel(fileModel))
            );
            return documentProvider.addDocument(flyWeightFile, providerType);
        } catch (UnsupportedDocumentTypeException | UnreadableDocumentException | PreexistedDocumentException e) {
            boolean result = fileProvider.removeFile(fileModel);
            logger.debug("Rollback file result=" + result);
            throw e;
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
            UnsupportedDocumentTypeException,
            FileFetchException,
            UnreadableDocumentException,
            PreexistedDocumentException {
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
