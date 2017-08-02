package org.openfact.models.es;

import org.openfact.models.FileModel;
import org.openfact.models.es.entity.DocumentEntity;
import org.w3c.dom.Document;

public interface DocumentMapper {

    DocumentEntity buildEntity(Document document, FileModel fileModel);

}
