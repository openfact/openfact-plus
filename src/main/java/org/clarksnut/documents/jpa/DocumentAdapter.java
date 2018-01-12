package org.clarksnut.documents.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentProviderType;
import org.clarksnut.documents.DocumentVersionModel;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.jpa.entity.DocumentVersionEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentAdapter implements DocumentModel, JpaModel<DocumentEntity> {

    private final EntityManager em;
    private final DocumentEntity document;

    public DocumentAdapter(EntityManager em, DocumentEntity document) {
        this.em = em;
        this.document = document;
    }

    public static DocumentEntity toEntity(DocumentModel model, EntityManager em) {
        if (model instanceof DocumentAdapter) {
            return ((DocumentAdapter) model).getEntity();
        }
        return em.getReference(DocumentEntity.class, model.getId());
    }

    @Override
    public DocumentEntity getEntity() {
        return document;
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
    public String getSupplierAssignedId() {
        return document.getSupplierAssignedId();
    }

    @Override
    public List<DocumentVersionModel> getVersions() {
        return document.getVersions().stream()
                .map(f -> new DocumentVersionAdapter(em, this, f))
                .collect(Collectors.toList());
    }

    @Override
    public DocumentVersionModel getCurrentVersion() {
        TypedQuery<DocumentVersionEntity> typedQuery = em.createNamedQuery("getCurrentDocumentVersionByDocumentId", DocumentVersionEntity.class);
        typedQuery.setParameter("documentId", document.getId());

        List<DocumentVersionEntity> resultList = typedQuery.getResultList();
        if (resultList.size() == 1) {
            return new DocumentVersionAdapter(em, this, resultList.get(0));
        } else if (resultList.size() == 0) {
            return null;
        } else {
            throw new IllegalStateException("Invalid number of results");
        }
    }

}
