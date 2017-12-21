package org.clarksnut.documents.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentProviderType;
import org.clarksnut.documents.DocumentVersionModel;
import org.clarksnut.documents.jpa.entity.DocumentVersionEntity;

import javax.persistence.EntityManager;
import java.util.Date;

public class DocumentVersionAdapter implements DocumentVersionModel, JpaModel<DocumentVersionEntity> {

    private final EntityManager em;
    private final DocumentModel document;
    private final DocumentVersionEntity documentVersion;

    public DocumentVersionAdapter(EntityManager em, DocumentModel document, DocumentVersionEntity documentVersion) {
        this.em = em;
        this.document = document;
        this.documentVersion = documentVersion;
    }

    public static DocumentVersionEntity toEntity(DocumentModel model, EntityManager em) {
        if (model instanceof DocumentVersionAdapter) {
            return ((DocumentVersionAdapter) model).getEntity();
        }
        return em.getReference(DocumentVersionEntity.class, model.getId());
    }

    @Override
    public DocumentVersionEntity getEntity() {
        return documentVersion;
    }

    @Override
    public String getId() {
        return documentVersion.getId();
    }

    @Override
    public boolean isCurrentVersion() {
        return documentVersion.isCurrentVersion();
    }

    @Override
    public DocumentModel getDocument() {
        return document;
    }

    @Override
    public String getType() {
        return documentVersion.getType();
    }

    @Override
    public String getAssignedId() {
        return documentVersion.getAssignedId();
    }

    @Override
    public String getFileId() {
        return documentVersion.getFileId();
    }

    @Override
    public String getFileProvider() {
        return documentVersion.getFileProvider();
    }

    @Override
    public Float getAmount() {
        return documentVersion.getAmount();
    }

    @Override
    public Float getTax() {
        return documentVersion.getTax();
    }

    @Override
    public String getCurrency() {
        return documentVersion.getCurrency();
    }

    @Override
    public Date getIssueDate() {
        return documentVersion.getIssueDate();
    }

    @Override
    public String getSupplierName() {
        return documentVersion.getSupplierName();
    }

    @Override
    public String getSupplierAssignedId() {
        return documentVersion.getSupplierAssignedId();
    }

    @Override
    public String getSupplierStreetAddress() {
        return documentVersion.getSupplierStreetAddress();
    }

    @Override
    public String getSupplierCity() {
        return documentVersion.getSupplierCity();
    }

    @Override
    public String getSupplierCountry() {
        return documentVersion.getSupplierCountry();
    }

    @Override
    public String getCustomerName() {
        return documentVersion.getCustomerName();
    }

    @Override
    public String getCustomerAssignedId() {
        return documentVersion.getCustomerAssignedId();
    }

    @Override
    public String getCustomerStreetAddress() {
        return documentVersion.getCustomerStreetAddress();
    }

    @Override
    public String getCustomerCity() {
        return documentVersion.getCustomerCity();
    }

    @Override
    public String getCustomerCountry() {
        return documentVersion.getCustomerCountry();
    }

    @Override
    public Date getCreatedAt() {
        return documentVersion.getCreatedAt();
    }

    @Override
    public Date getUpdatedAt() {
        return documentVersion.getUpdatedAt();
    }

    @Override
    public DocumentProviderType getProvider() {
        return documentVersion.getProvider();
    }

    @Override
    public boolean isVerified() {
        return documentVersion.isVerified();
    }

}
