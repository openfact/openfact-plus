package org.clarksnut.files;

import org.clarksnut.files.exceptions.FileFetchException;

public class FlyWeightXmlUBLFileModel extends FlyWeightXmlFileModel implements XmlUBLFileModel {

    protected String documentType;

    public FlyWeightXmlUBLFileModel(XmlFileModel file) throws FileFetchException {
        super(file);
        String documentType = XmlUBLFileModel.getDocumentType(file);
        if (documentType == null) {
            throw new IllegalStateException("File is not UBL");
        }
        this.documentType = documentType;
    }

    @Override
    public String getDocumentType() {
        return this.documentType;
    }

}
