package org.openfact.documents.jpa;

import org.openfact.documents.DocumentModel;
import org.openfact.documents.DocumentProviderType;
import org.openfact.documents.jpa.entity.DocumentEntity;
import org.openfact.models.SpaceModel;
import org.openfact.models.db.JpaModel;
import org.openfact.models.db.jpa.SpaceAdapter;

import javax.persistence.EntityManager;
import java.util.*;
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
    public String getFileId() {
        return document.getFileId();
    }

    @Override
    public Float getAmount() {
        return document.getAmount();
    }

    @Override
    public String getCurrency() {
        return document.getCurrency();
    }

    @Override
    public Date getIssueDate() {
        return document.getIssueDate();
    }

    @Override
    public String getSupplierName() {
        return document.getSupplierName();
    }

    @Override
    public String getSupplierAssignedId() {
        return document.getSupplierAssignedId();
    }

    @Override
    public String getCustomerName() {
        return document.getCustomerName();
    }

    @Override
    public String getCustomerAssignedId() {
        return document.getCustomerAssignedId();
    }

    @Override
    public Map<String, String> getTags() {
        Map<String, String> config = new HashMap<>();
        config.putAll(document.getTags());
        return Collections.unmodifiableMap(config);
    }

    @Override
    public Date getCreatedAt() {
        return document.getCreatedAt();
    }

    @Override
    public Date getUpdatedAt() {
        return document.getUpdatedAt();
    }

    @Override
    public Set<SpaceModel> getSpaces() {
        return document.getSpaces().stream()
                .map(f -> new SpaceAdapter(em, f.getSpace()))
                .collect(Collectors.toSet());
    }

    @Override
    public DocumentProviderType getProvider() {
        return document.getProvider();
    }

}
