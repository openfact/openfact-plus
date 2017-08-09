package org.openfact.services.managers;

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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
            document = toDocument(bytes);
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
            document = toDocument(inputStream);
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
            byte[] file = toByteArray(document);
            fileModel = fileProvider.addFile(file, ".xml");
        } catch (TransformerException e) {
            throw new ParseExceptionModel("Could not parse Document to bytes[]", e);
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

    private Document toDocument(byte[] bytes) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(bytes));
    }

    private byte[] toByteArray(Document document) throws TransformerException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(out);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(new DOMSource(document), result);
        return out.toByteArray();
    }

}
