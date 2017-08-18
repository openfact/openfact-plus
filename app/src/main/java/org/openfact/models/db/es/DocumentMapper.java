package org.openfact.models.db.es;

import org.openfact.models.FileModel;
import org.openfact.models.db.es.entity.DocumentEntity;

public interface DocumentMapper {

    DocumentEntity buildEntity(FileModel file);

}
