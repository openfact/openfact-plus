package org.clarksnut.models.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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

public class ClarksnutModelUtils {

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static String getDocumentType(byte[] bytes) throws Exception {
        return getDocumentType(ClarksnutModelUtils.toDocument(bytes));
    }

    public static String getDocumentType(Document document) throws Exception {
        Element documentElement = document.getDocumentElement();
        return documentElement.getTagName();
    }

    public static Document toDocument(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
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

    public static <T> T unmarshall(Document document, Class<T> tClass) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(tClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<T> jaxbElement = unmarshaller.unmarshal(document, tClass);
        return jaxbElement.getValue();
    }

}
