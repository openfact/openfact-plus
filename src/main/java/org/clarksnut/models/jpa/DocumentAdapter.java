package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.DocumentModel;
import org.clarksnut.models.DocumentVersionModel;
import org.clarksnut.models.jpa.entity.DocumentEntity;
import org.clarksnut.models.jpa.entity.DocumentVersionEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public Float getAmount() {
        return entity.getAmount();
    }

    @Override
    public Float getTax() {
        return entity.getTax();
    }

    @Override
    public String getCurrency() {
        return entity.getCurrency();
    }

    @Override
    public Date getIssueDate() {
        return entity.getIssueDate();
    }

    @Override
    public String getSupplierName() {
        return entity.getSupplierName();
    }

    @Override
    public String getSupplierStreetAddress() {
        return entity.getSupplierStreetAddress();
    }

    @Override
    public String getSupplierCity() {
        return entity.getSupplierCity();
    }

    @Override
    public String getSupplierCountry() {
        return entity.getSupplierCountry();
    }

    @Override
    public String getCustomerName() {
        return entity.getCustomerName();
    }

    @Override
    public String getCustomerStreetAddress() {
        return entity.getCustomerStreetAddress();
    }

    @Override
    public String getCustomerCity() {
        return entity.getCustomerCity();
    }

    @Override
    public String getCustomerCountry() {
        return entity.getCustomerCountry();
    }


    @Override
    public List<DocumentVersionModel> getVersions() {
        return entity.getVersions().stream()
                .map(f -> new DocumentVersionAdapter(em, this, f))
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getUserStars() {
        return new HashSet<>(entity.getUserStarts());
    }

    @Override
    public void setUserStarts(Set<String> userIds) {
        entity.setUserStarts(userIds);
    }

    @Override
    public void addStart(String userId) {
        entity.getUserStarts().add(userId);
    }

    @Override
    public boolean removeStart(String userId) {
        return entity.getUserStarts().remove(userId);
    }

    @Override
    public Set<String> getUserViews() {
        return entity.getUserViews();
    }

    @Override
    public void setUserViews(Set<String> userIds) {
        entity.setUserViews(userIds);
    }

    @Override
    public void addViewed(String userId) {
        entity.getUserViews().add(userId);
    }

    @Override
    public boolean removeViewed(String userId) {
        return entity.getUserViews().remove(userId);
    }

    @Override
    public Set<String> getUserChecks() {
        return entity.getUserChecks();
    }

    @Override
    public void setUserChecks(Set<String> userIds) {
        entity.setUserChecks(userIds);
    }

    @Override
    public void addCheck(String userId) {
        entity.getUserChecks().add(userId);
    }

    @Override
    public boolean removeCheck(String userId) {
        return entity.getUserChecks().remove(userId);
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
    public Date getCreatedAt() {
        return entity.getCreatedAt();
    }

    @Override
    public Date getUpdatedAt() {
        return entity.getUpdatedAt();
    }

}
