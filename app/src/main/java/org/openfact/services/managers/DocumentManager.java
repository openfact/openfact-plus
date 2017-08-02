package org.openfact.services.managers;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.openfact.models.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
     * @param inputStream java.io.InputStream
     * @throws ParseExceptionModel in case InputStream passed could not be processed
     * @throws StorageException    in case could not be persist file on storage
     * @throws ModelException      in case unexpected error happens
     */
    public DocumentModel importDocument(InputStream inputStream) throws ParseExceptionModel, StorageException {
        Document document;
        try {
            document = toDocument(inputStream);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new ParseExceptionModel("Could not parse input stream to xml document", e);
        }

        // Persist File
        FileModel fileModel;
        try {
            byte[] file = IOUtils.toByteArray(inputStream);
            fileModel = fileProvider.addFile(file, ".xml");
        } catch (IOException e) {
            throw new ParseExceptionModel("Could not buildEntity inputStream", e);
        }

        // Persist Document
        try {
            return documentProvider.addDocument(document, fileModel);
        } catch (ModelException e) {
            boolean result = fileProvider.removeFile(fileModel);
            logger.infof("Rollback file result={}", result);
            throw new ModelException("Could not persist document model", e);
        }
    }

    public static Document toDocument(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(inputStream));
    }

}
