package org.clarksnut.documents;

import org.clarksnut.documents.jpa.entity.DocumentEntity;

public interface GenericDocument {

    DocumentEntity getEntity();

    Object getJaxb();

}
