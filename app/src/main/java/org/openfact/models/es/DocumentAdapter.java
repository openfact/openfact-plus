package org.openfact.models.es;

import org.openfact.models.DocumentModel;
import org.openfact.models.es.entity.DocumentEntity;

import javax.persistence.EntityManager;
import java.util.Map;

public class DocumentAdapter implements DocumentModel {

    private final EntityManager em;
    private final DocumentEntity persistedDocument;

    public DocumentAdapter(EntityManager em, DocumentEntity persistedDocument) {
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
    public Map<String, Object> getAttributes() {
        return persistedDocument.getAttributes();
    }

    @Override
    public String getFileId() {
        return persistedDocument.getFileId();
    }

}
