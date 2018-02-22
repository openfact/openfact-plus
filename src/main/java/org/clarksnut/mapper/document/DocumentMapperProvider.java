package org.clarksnut.mapper.document;

import org.clarksnut.models.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.files.XmlUBLFileModel;

public interface DocumentMapperProvider {

    String getGroup();

    String getSupportedDocumentType();

    DocumentMapped map(XmlUBLFileModel file) throws ImpossibleToUnmarshallException;

}
