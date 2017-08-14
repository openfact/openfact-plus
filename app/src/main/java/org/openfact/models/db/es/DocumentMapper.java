package org.openfact.models.db.es;

import org.openfact.models.FileModel;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.w3c.dom.Document;

public interface DocumentMapper {

    DocumentEntity buildEntity(Document document, FileModel fileModel);

}
