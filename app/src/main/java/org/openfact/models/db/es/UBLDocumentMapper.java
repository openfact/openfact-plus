package org.openfact.models.db.es;

import org.openfact.models.FileModel;
import org.openfact.models.db.es.entity.UBLDocumentEntity;

public interface UBLDocumentMapper {

    UBLDocumentEntity map(FileModel file);

}
