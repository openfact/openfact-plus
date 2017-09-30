package org.openfact.models.db.es;

import org.openfact.models.XmlUBLFileModel;

public interface DocumentReader {

    String getSupportedDocumentType();

    int getPriority();

    GenericDocument read(XmlUBLFileModel file);

}
