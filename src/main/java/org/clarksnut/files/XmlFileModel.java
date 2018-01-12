package org.clarksnut.files;

import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface XmlFileModel extends FileModel {

    Document getDocument();

    static Document isXmlFile(FileModel file)  {
        byte[] bytes = file.getFile();
        try {
            return ClarksnutModelUtils.toDocument(bytes);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return null;
        }
    }

}
