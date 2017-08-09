package org.openfact.models.storage.db.es;

import org.openfact.models.FileModel;
import org.openfact.models.storage.db.es.entity.DocumentEntity;
import org.w3c.dom.Document;

public interface DocumentMapper {

    DocumentEntity buildEntity(Document document, FileModel fileModel);

}
