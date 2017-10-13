package org.openfact.documents;

import org.openfact.documents.entity.DocumentEntity;

public interface GenericDocument {

    DocumentEntity getEntity();

    Object getJaxb();

}
