package org.clarksnut.documents.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentProviderType;
import org.clarksnut.documents.DocumentVersionModel;
import org.clarksnut.documents.jpa.entity.DocumentEntity;

import javax.persistence.EntityManager;
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
    public boolean hasChanged() {
        return document.isChanged();
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
    public String getFileProvider() {
        return document.getFileProvider();
    }

    @Override
    public Float getAmount() {
        return document.getAmount();
    }

    @Override
    public Float getTax() {
        return document.getTax();
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
    public String getSupplierStreetAddress() {
        return document.getSupplierStreetAddress();
    }

    @Override
    public String getSupplierCity() {
        return document.getSupplierCity();
    }

    @Override
    public String getSupplierCountry() {
        return document.getSupplierCountry();
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
    public String getCustomerStreetAddress() {
        return document.getCustomerStreetAddress();
    }

    @Override
    public String getCustomerCity() {
        return document.getCustomerCity();
    }

    @Override
    public String getCustomerCountry() {
        return document.getCustomerCountry();
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
    public DocumentProviderType getProvider() {
        return document.getProvider();
    }

    @Override
    public boolean isVerified() {
        return document.isVerified();
    }

    @Override
    public List<DocumentVersionModel> getVersions() {
        return document.getVersions().stream()
                .map(f -> new DocumentVersionAdapter(em, this, f))
                .collect(Collectors.toList());
    }

}
