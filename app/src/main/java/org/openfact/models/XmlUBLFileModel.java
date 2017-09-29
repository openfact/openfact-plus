package org.openfact.models;

import org.openfact.models.utils.OpenfactModelUtils;

public interface XmlUBLFileModel extends XmlFileModel {

    String getDocumentType();

    static String getDocumentType(XmlFileModel file) throws ModelFetchException {
        try {
            return OpenfactModelUtils.getDocumentType(file.getFile());
        } catch (Exception e) {
            return null;
        }
    }

}
