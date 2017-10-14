package org.openfact.files;

import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;

public interface XmlUBLFileModel extends XmlFileModel {

    String getDocumentType();

    static String getDocumentType(XmlFileModel file) throws FileFetchException {
        try {
            return OpenfactModelUtils.getDocumentType(file.getFile());
        } catch (Exception e) {
            return null;
        }
    }

}
