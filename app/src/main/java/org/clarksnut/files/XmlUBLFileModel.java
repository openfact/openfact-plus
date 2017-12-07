package org.clarksnut.files;

import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;

public interface XmlUBLFileModel extends XmlFileModel {

    String getDocumentType();

    static String getDocumentType(XmlFileModel file) throws FileFetchException {
        try {
            return ClarksnutModelUtils.getDocumentType(file.getFile());
        } catch (Exception e) {
            return null;
        }
    }

}
