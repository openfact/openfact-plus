package org.openfact.services.managers;

import org.apache.commons.io.IOUtils;
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
     * @throws ModelParseException   in case InputStream passed could not be processed
     * @throws ModelStorageException in case could not be persist file on storage
     * @throws ModelException        in case unexpected error happens
     */
    public DocumentModel importDocument(byte[] bytes) throws ModelStorageException, ModelUnsupportedTypeException, ModelFetchException, ModelParseException {
        FileModel fileModel = fileProvider.addFile(bytes, ".xml");

        try {
            FlyWeightXmlUBLFileModel flyWeightFile = new FlyWeightXmlUBLFileModel(
                    new FlyWeightXmlFileModel(
                            new FlyWeightFileModel(fileModel))
            );
            return documentProvider.addDocument(flyWeightFile);
        } catch (ModelUnsupportedTypeException | ModelParseException e) {
            boolean result = fileProvider.removeFile(fileModel);
            logger.debug("Rollback file result=" + result);
            throw e;
        }
    }

    /**
     * @param inputStream java.io.InputStream
     * @throws ModelParseException   in case InputStream passed could not be processed
     * @throws ModelStorageException in case could not be persist file on storage
     * @throws ModelException        in case unexpected error happens
     */
    public DocumentModel importDocument(InputStream inputStream) throws IOException, ModelStorageException, ModelUnsupportedTypeException, ModelFetchException, ModelParseException {
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
        } catch (ModelStorageException e) {
            try {
                fileProvider.removeFile(file);
            } catch (ModelStorageException e1) {
                logger.error("Could not remove file:" + file.getId());
                logger.warn("Transaction was not rolled back");
            }
        }

        return true;
    }

}
