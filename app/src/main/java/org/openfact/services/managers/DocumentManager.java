package org.openfact.services.managers;

import org.jboss.logging.Logger;
import org.openfact.models.*;
import org.openfact.models.utils.OpenfactModelUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
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
     * @throws ParseExceptionModel in case InputStream passed could not be processed
     * @throws StorageException    in case could not be persist file on storage
     * @throws ModelException      in case unexpected error happens
     */
    public DocumentModel importDocument(byte[] bytes) throws ParseExceptionModel, StorageException {
        Document document;
        try {
            document = OpenfactModelUtils.toDocument(bytes);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new ParseExceptionModel("Could not parse bytes[] to Document", e);
        }

        return importDocument(document);
    }

    /**
     * @param inputStream java.io.InputStream
     * @throws ParseExceptionModel in case InputStream passed could not be processed
     * @throws StorageException    in case could not be persist file on storage
     * @throws ModelException      in case unexpected error happens
     */
    public DocumentModel importDocument(InputStream inputStream) throws ParseExceptionModel, StorageException {
        Document document;
        try {
            document = OpenfactModelUtils.toDocument(inputStream);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new ParseExceptionModel("Could not parse inputStream to Document", e);
        }

        return importDocument(document);
    }

    /**
     * @param document
     * @throws ParseExceptionModel in case InputStream passed could not be processed
     * @throws StorageException    in case could not be persist file on storage
     * @throws ModelException      in case unexpected error happens
     */
    public DocumentModel importDocument(Document document) throws ParseExceptionModel, StorageException {
        // Persist File
        FileModel fileModel;
        try {
            byte[] file = OpenfactModelUtils.toByteArray(document);
            fileModel = fileProvider.addFile(file, ".xml");
        } catch (TransformerException e) {
            throw new ParseExceptionModel("Could not parse Document to bytes[]", e);
        }

        // Persist Document
        try {
            return documentProvider.addDocument(fileModel);
        } catch (ModelException e) {
            boolean result = fileProvider.removeFile(fileModel);
            logger.infof("Rollback file result={}", result);
            throw new ModelException("Could not persist document model", e);
        }
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
        } catch (StorageException e) {
            try {
                fileProvider.removeFile(file);
            } catch (StorageException e1) {
                logger.error("Could not remove file:" + file.getId());
                logger.warn("Transaction was not rolled back");
            }
        }

        return true;
    }

}
