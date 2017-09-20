package org.openfact.models.db.es;

import org.openfact.models.UBLDocumentModel;
import org.openfact.models.db.es.entity.UBLDocumentEntity;

import javax.persistence.EntityManager;

public class UBLDocumentAdapter implements UBLDocumentModel {

    private final EntityManager em;
    private final UBLDocumentEntity persistedDocument;

    public UBLDocumentAdapter(EntityManager em, UBLDocumentEntity persistedDocument) {
        this.em = em;
        this.persistedDocument = persistedDocument;
    }

    @Override
    public String getId() {
        return persistedDocument.getId();
    }

    @Override
    public String getType() {
        return persistedDocument.getType();
    }

    @Override
    public String getAssignedId() {
        return persistedDocument.getAssignedId();
    }

    @Override
    public String getFileId() {
        return persistedDocument.getFileId();
    }

}
