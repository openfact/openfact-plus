package org.clarksnut.mapper.document;

import org.clarksnut.documents.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.files.XmlUBLFileModel;

public interface DocumentMapperProvider {

    String getGroup();

    String getSupportedDocumentType();

    DocumentMapped map(XmlUBLFileModel file) throws ImpossibleToUnmarshallException;

}
