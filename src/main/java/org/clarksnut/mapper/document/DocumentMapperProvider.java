package org.clarksnut.mapper.document;

import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.exceptions.ImpossibleToUnmarshallException;

public interface DocumentMapperProvider {

    String getGroup();

    String getSupportedDocumentType();

    DocumentMapped map(XmlUBLFileModel file) throws ImpossibleToUnmarshallException;

}
