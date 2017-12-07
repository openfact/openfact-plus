package org.clarksnut.documents.jpa;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentProviderType;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.models.db.JpaModel;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.addAll(document.getTags());
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public void setTags(Set<String> tags) {
        document.setTags(tags);
    }

    @Override
    public boolean isStarred() {
        return document.isStarred();
    }

    @Override
    public void setStarred(boolean starred) {
        document.setStarred(starred);
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

}
