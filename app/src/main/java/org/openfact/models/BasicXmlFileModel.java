package org.openfact.models;

import org.openfact.models.utils.OpenfactModelUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class BasicXmlFileModel implements XmlFileModel {

    protected final FileModel file;

    public BasicXmlFileModel(FileModel file) throws ModelFetchException {
        if (!isXmlFile(file)) {
            throw new IllegalStateException("Can not create a xml file from a invalid document");
        }
        this.file = file;
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
    public String getExtension() {
        return file.getExtension();
    }

    @Override
    public byte[] getFile() throws ModelFetchException {
        return file.getFile();
    }

    @Override
    public boolean isXmlFile(FileModel file) throws ModelFetchException {
        boolean result;
        byte[] bytes = file.getFile();
        try {
            OpenfactModelUtils.toDocument(bytes);
            result = true;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            result = false;
        }
        return result;
    }

}
