package org.openfact.models.db.es;

import org.openfact.models.FileModel;

public interface DocumentReader {

    GenericDocument read(FileModel file);

}
