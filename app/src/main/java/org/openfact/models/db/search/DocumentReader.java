package org.openfact.models.db.search;

import org.openfact.models.XmlUBLFileModel;

public interface DocumentReader {

    String getSupportedDocumentType();

    int getPriority();

    GenericDocument read(XmlUBLFileModel file);

}
