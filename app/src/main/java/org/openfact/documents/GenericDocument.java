package org.openfact.documents;

import org.openfact.documents.jpa.entity.DocumentEntity;

public interface GenericDocument {

    DocumentEntity getEntity();

    Object getJaxb();

}
