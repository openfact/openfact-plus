package org.openfact.models;

import org.w3c.dom.Document;

public class FlyWeightXmlFileModel implements XmlFileModel {

    protected final FileModel file;
    protected Document document;

    public FlyWeightXmlFileModel(FileModel file) throws ModelFetchException {
        Document document = XmlFileModel.isXmlFile(file);
        if (document == null) {
            throw new IllegalStateException("File is not xml");
        }
        this.document = document;
        this.file = file;
    }

    @Override
    public String getId() {
        return this.file.getId();
    }

    @Override
    public String getFilename() {
        return this.file.getFilename();
    }

    @Override
    public String getExtension() {
        return this.file.getExtension();
    }

    @Override
    public byte[] getFile() throws ModelFetchException {
        return this.file.getFile();
    }

    @Override
    public Document getDocument() {
        return this.document;
    }

}
