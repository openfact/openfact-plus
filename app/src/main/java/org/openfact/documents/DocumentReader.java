package org.openfact.documents;

import org.openfact.files.XmlUBLFileModel;

public interface DocumentReader {

    String getSupportedDocumentType();

    int getPriority();

    GenericDocument read(XmlUBLFileModel file);

}
