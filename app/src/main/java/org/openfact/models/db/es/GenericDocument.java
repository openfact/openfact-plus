package org.openfact.models.db.es;

import org.openfact.models.db.es.entity.DocumentEntity;

public interface GenericDocument {

    DocumentEntity getEntity();

    Object getType();

}
