package org.openfact.models.utils;

import org.openfact.models.OpenfactSession;
import org.openfact.models.OpenfactSessionFactory;
import org.openfact.models.OpenfactSessionTask;
import org.openfact.transaction.OpenfactTransaction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
import java.util.UUID;

public class OpenfactModelUtils {

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static String getDocumentType(byte[] bytes) throws Exception {
        Document document = OpenfactModelUtils.toDocument(bytes);
        Element documentElement = document.getDocumentElement();
        return documentElement.getTagName();
    }

    public static Document toDocument(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(inputStream));
    }

    public static Document toDocument(byte[] bytes) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(bytes));
    }

    public static byte[] toByteArray(Document document) throws TransformerException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(out);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(new DOMSource(document), result);
        return out.toByteArray();
    }

    /**
     * Wrap given runnable job into OpenfactTransaction.
     *
     * @param task
     */
    public static void runJobInTransaction(OpenfactSessionFactory factory, OpenfactSessionTask task) {
        OpenfactSession session = factory.create();
        OpenfactTransaction tx = session.getTransactionManager();
        try {
            tx.begin();
            task.run();

            if (tx.isActive()) {
                if (tx.getRollbackOnly()) {
                    tx.rollback();
                } else {
                    tx.commit();
                }
            }
        } catch (RuntimeException re) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw re;
        } finally {
            session.close();
        }
    }

}
