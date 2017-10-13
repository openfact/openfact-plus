package org.openfact.models.db.search;

import org.openfact.models.db.search.entity.DocumentEntity;

public interface GenericDocument {

    DocumentEntity getEntity();

    Object getJaxb();

}
