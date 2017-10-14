package org.openfact.files;

import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface XmlFileModel extends FileModel {

    Document getDocument();

    static Document isXmlFile(FileModel file) throws FileFetchException {
        byte[] bytes = file.getFile();
        try {
            return OpenfactModelUtils.toDocument(bytes);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return null;
        }
    }

}
