package org.clarksnut.files;

import org.w3c.dom.Document;

public class FlyWeightXmlFileModel implements XmlFileModel {

    protected final FileModel model;
    protected Document document;

    public FlyWeightXmlFileModel(FileModel model)  {
        Document document = XmlFileModel.isXmlFile(model);
        if (document == null) {
            throw new IllegalStateException("File is not xml");
        }
        this.document = document;
        this.model = model;
    }

    @Override
    public String getId() {
        return this.model.getId();
    }

    @Override
    public String getFilename() {
        return model.getFilename();
    }

    @Override
    public byte[] getFileAsBytes()  {
        return this.model.getFileAsBytes();
    }

    @Override
    public Document getDocument() {
        return this.document;
    }

}
