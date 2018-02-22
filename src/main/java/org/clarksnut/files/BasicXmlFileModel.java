package org.clarksnut.files;

import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.w3c.dom.Document;

public class BasicXmlFileModel implements XmlFileModel {

    protected final FileModel file;

    public BasicXmlFileModel(FileModel file) {
        this.file = file;
    }

    @Override
    public Document getDocument() throws Exception {
        return ClarksnutModelUtils.toDocument(file.getFile());
    }

    @Override
    public String getId() {
        return file.getId();
    }

    @Override
    public String getFilename() {
        return file.getFilename();
    }

    @Override
    public byte[] getFile() {
        return file.getFile();
    }

    @Override
    public long getChecksum() {
        return file.getChecksum();
    }
}
