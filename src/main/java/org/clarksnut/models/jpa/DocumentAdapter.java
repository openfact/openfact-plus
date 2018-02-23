package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.DocumentModel;
import org.clarksnut.models.DocumentVersionModel;
import org.clarksnut.models.IndexedDocumentModel;
import org.clarksnut.models.jpa.entity.DocumentEntity;
import org.clarksnut.models.jpa.entity.DocumentVersionEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentAdapter implements DocumentModel, JpaModel<DocumentEntity> {

    private final EntityManager em;
    private final DocumentEntity entity;

    public DocumentAdapter(EntityManager em, DocumentEntity document) {
        this.em = em;
        this.entity = document;
    }

    public static DocumentEntity toEntity(DocumentModel model, EntityManager em) {
        if (model instanceof DocumentAdapter) {
            return ((DocumentAdapter) model).getEntity();
        }
        return em.getReference(DocumentEntity.class, model.getId());
    }

    @Override
    public DocumentEntity getEntity() {
        return entity;
    }

    @Override
    public String getId() {
        return entity.getId();
    }

    @Override
    public String getType() {
        return entity.getType();
    }

    @Override
    public String getAssignedId() {
        return entity.getAssignedId();
    }

    @Override
    public String getSupplierAssignedId() {
        return entity.getSupplierAssignedId();
    }

    @Override
    public String getCustomerAssignedId() {
        return entity.getCustomerAssignedId();
    }

    @Override
    public List<DocumentVersionModel> getVersions() {
        return entity.getVersions().stream()
                .map(f -> new DocumentVersionAdapter(em, this, f))
                .collect(Collectors.toList());
    }

    @Override
    public DocumentVersionModel getCurrentVersion() {
        TypedQuery<DocumentVersionEntity> typedQuery = em.createNamedQuery("getCurrentDocumentVersionByDocumentId", DocumentVersionEntity.class);
        typedQuery.setParameter("documentId", entity.getId());

        List<DocumentVersionEntity> resultList = typedQuery.getResultList();
        if (resultList.size() == 1) {
            return new DocumentVersionAdapter(em, this, resultList.get(0));
        } else if (resultList.size() == 0) {
            return null;
        } else {
            throw new IllegalStateException("Invalid number of results");
        }
    }

    @Override
    public IndexedDocumentModel getIndexedDocument() {
        return new IndexedDocumentAdapter(em, entity.getIndexedDocument());
    }

    @Override
    public Date getCreatedAt() {
        return entity.getCreatedAt();
    }

    @Override
    public Date getUpdatedAt() {
        return entity.getUpdatedAt();
    }

}
