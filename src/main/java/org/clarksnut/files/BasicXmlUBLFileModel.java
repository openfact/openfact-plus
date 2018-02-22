package org.clarksnut.files;

import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.w3c.dom.Document;

public class BasicXmlUBLFileModel implements XmlUBLFileModel {

    private final XmlFileModel xmlFile;

    public BasicXmlUBLFileModel(XmlFileModel xmlFile) {
        this.xmlFile = xmlFile;
    }

    @Override
    public String getDocumentType() throws Exception {
        return ClarksnutModelUtils.getDocumentType(xmlFile.getDocument());
    }

    @Override
    public Document getDocument() throws Exception {
        return xmlFile.getDocument();
    }

    @Override
    public String getId() {
        return xmlFile.getId();
    }

    @Override
    public String getFilename() {
        return xmlFile.getFilename();
    }

    @Override
    public byte[] getFile() {
        return xmlFile.getFile();
    }

    @Override
    public long getChecksum() {
        return xmlFile.getChecksum();
    }
}
