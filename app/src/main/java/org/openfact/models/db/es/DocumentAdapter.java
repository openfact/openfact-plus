package org.openfact.models.db.es;

import org.openfact.models.DocumentModel;
import org.openfact.models.db.es.entity.DocumentEntity;

import javax.persistence.EntityManager;

public class DocumentAdapter implements DocumentModel {

    private final EntityManager em;
    private final DocumentEntity document;

    public DocumentAdapter(EntityManager em, DocumentEntity document) {
        this.em = em;
        this.document = document;
    }

    @Override
    public String getId() {
        return document.getId();
    }

    @Override
    public String getType() {
        return document.getType();
    }

    @Override
    public String getAssignedId() {
        return document.getAssignedId();
    }

    @Override
    public String getFileId() {
        return document.getFileId();
    }

}
