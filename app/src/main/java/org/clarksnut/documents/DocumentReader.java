package org.clarksnut.documents;

import org.clarksnut.files.XmlUBLFileModel;

public interface DocumentReader {

    String getSupportedDocumentType();

    int getPriority();

    GenericDocument read(XmlUBLFileModel file);

}
