package org.openfact.models.db.es;

import org.openfact.models.ModelFetchException;
import org.openfact.models.ModelParseException;
import org.openfact.models.XmlUblFileModel;

public interface DocumentReader {

    GenericDocument read(XmlUblFileModel file) throws ModelFetchException, ModelParseException;

}
