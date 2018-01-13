package org.clarksnut.documents.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.documents.IndexedDocumentModel;
import org.clarksnut.documents.jpa.entity.IndexedDocumentEntity;
import org.clarksnut.models.UserModel;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class IndexedDocumentAdapter implements IndexedDocumentModel, JpaModel<IndexedDocumentEntity> {

    private final EntityManager em;
    private final UserModel user;
    private final IndexedDocumentEntity entity;

    public IndexedDocumentAdapter(EntityManager em, UserModel user, IndexedDocumentEntity entity) {
        this.em = em;
        this.user = user;
        this.entity = entity;
    }

    @Override
    public IndexedDocumentEntity getEntity() {
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
    public Date getIssueDate() {
        return entity.getIssueDate();
    }

    @Override
    public String getCurrency() {
        return entity.getCurrency();
    }

    @Override
    public Float getTax() {
        return entity.getTax();
    }

    @Override
    public Float getAmount() {
        return entity.getAmount();
    }

    @Override
    public String getSupplierName() {
        return entity.getSupplierName();
    }

    @Override
    public String getSupplierAssignedId() {
        return entity.getSupplierAssignedId();
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
    public String getCustomerAssignedId() {
        return entity.getCustomerAssignedId();
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
    public Date getCreatedAt() {
        return entity.getCreatedAt();
    }

    @Override
    public Date getUpdatedAt() {
        return entity.getUpdatedAt();
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
        return new HashSet<>(entity.getUserViews());
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
        return new HashSet<>(entity.getUserChecks());
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

}
