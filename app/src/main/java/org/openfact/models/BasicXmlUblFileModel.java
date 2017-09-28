package org.openfact.models;

import org.openfact.models.utils.OpenfactModelUtils;

public class BasicXmlUblFileModel extends BasicXmlFileModel implements XmlUblFileModel {

    public BasicXmlUblFileModel(FileModel file) throws ModelFetchException {
        super(file);
    }

    @Override
    public boolean isUblFile(XmlFileModel file) throws ModelFetchException {
        try {
            OpenfactModelUtils.getDocumentType(file.getFile());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
