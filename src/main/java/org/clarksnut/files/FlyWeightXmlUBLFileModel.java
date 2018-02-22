package org.clarksnut.files;

import org.w3c.dom.Document;

public class FlyWeightXmlUBLFileModel implements XmlUBLFileModel {

    private final XmlUBLFileModel ublFile;
    protected String documentType;

    public FlyWeightXmlUBLFileModel(XmlUBLFileModel ublFile) {
        this.ublFile = ublFile;
    }

    @Override
    public String getDocumentType() throws Exception {
        if (documentType == null) {
            this.documentType = ublFile.getDocumentType();
        }
        return this.documentType;
    }

    @Override
    public Document getDocument() throws Exception {
        return ublFile.getDocument();
    }

    @Override
    public String getId() {
        return ublFile.getId();
    }

    @Override
    public String getFilename() {
        return ublFile.getFilename();
    }

    @Override
    public byte[] getFile() {
        return ublFile.getFile();
    }

    @Override
    public long getChecksum() {
        return ublFile.getChecksum();
    }
}
