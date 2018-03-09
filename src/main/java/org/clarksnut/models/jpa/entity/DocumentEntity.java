package org.clarksnut.models.jpa.entity;

import org.clarksnut.common.jpa.CreatableEntity;
import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatableEntity;
import org.clarksnut.common.jpa.UpdatedAtListener;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "cn_document", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type", "assigned_id", "supplier_assigned_id"})
})
@NamedQueries({
        @NamedQuery(name = "getAllDocuments", query = "select d from DocumentEntity d"),
        @NamedQuery(name = "getDocumentByTypeAssignedIdAndSupplierAssignedId", query = "select d from DocumentEntity d where d.type = :type and d.assignedId = :assignedId and d.supplierAssignedId =:supplierAssignedId")
})
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
public class DocumentEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull(message = "type should not be null")
    @Column(name = "type")
    private String type;

    @NotNull(message = "assignedId should not be null")
    @Column(name = "assigned_id")
    private String assignedId;

    @NotNull(message = "supplier assigned id should not be null")
    @Column(name = "supplier_assigned_id")
    private String supplierAssignedId;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "supplier_street_address")
    private String supplierStreetAddress;

    @Column(name = "supplier_city")
    private String supplierCity;

    @Column(name = "supplier_country")
    private String supplierCountry;

    @Column(name = "customer_assigned_id")
    private String customerAssignedId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_street_address")
    private String customerStreetAddress;

    @Column(name = "customer_city")
    private String customerCity;

    @Column(name = "customer_country")
    private String customerCountry;

    @Column(name = "currency")
    private String currency;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "issue_date")
    private Date issueDate;

    @Digits(integer = 10, fraction = 4, message = "amount has incorrect number of integer/fraction")
    @Type(type = "org.hibernate.type.DoubleType")
    @Column(name = "amount")
    private Double amount;

    @Digits(integer = 10, fraction = 4, message = "tax has incorrect number of integer/fraction")
    @Type(type = "org.hibernate.type.DoubleType")
    @Column(name = "tax")
    private Double tax;

    @ElementCollection
    @Column(name = "value")
    @CollectionTable(name = "user_starts", joinColumns = {@JoinColumn(name = "document_id")})
    private Set<String> userStarts = new HashSet<>();

    @ElementCollection
    @Column(name = "value")
    @CollectionTable(name = "user_views", joinColumns = {@JoinColumn(name = "document_id")})
    private Set<String> userViews = new HashSet<>();

    @ElementCollection
    @Column(name = "value")
    @CollectionTable(name = "user_checks", joinColumns = {@JoinColumn(name = "document_id")})
    private Set<String> userChecks = new HashSet<>();

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    private List<DocumentVersionEntity> versions = new ArrayList<>();

    @NotNull(message = "createdAt should not be null")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @NotNull(message = "updatedAt should not be null")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    public String getSupplierAssignedId() {
        return supplierAssignedId;
    }

    public void setSupplierAssignedId(String supplierAssignedId) {
        this.supplierAssignedId = supplierAssignedId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierStreetAddress() {
        return supplierStreetAddress;
    }

    public void setSupplierStreetAddress(String supplierStreetAddress) {
        this.supplierStreetAddress = supplierStreetAddress;
    }

    public String getSupplierCity() {
        return supplierCity;
    }

    public void setSupplierCity(String supplierCity) {
        this.supplierCity = supplierCity;
    }

    public String getSupplierCountry() {
        return supplierCountry;
    }

    public void setSupplierCountry(String supplierCountry) {
        this.supplierCountry = supplierCountry;
    }

    public String getCustomerAssignedId() {
        return customerAssignedId;
    }

    public void setCustomerAssignedId(String customerAssignedId) {
        this.customerAssignedId = customerAssignedId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerStreetAddress() {
        return customerStreetAddress;
    }

    public void setCustomerStreetAddress(String customerStreetAddress) {
        this.customerStreetAddress = customerStreetAddress;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public String getCustomerCountry() {
        return customerCountry;
    }

    public void setCustomerCountry(String customerCountry) {
        this.customerCountry = customerCountry;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Set<String> getUserStarts() {
        return userStarts;
    }

    public void setUserStarts(Set<String> userStarts) {
        this.userStarts = userStarts;
    }

    public Set<String> getUserViews() {
        return userViews;
    }

    public void setUserViews(Set<String> userViews) {
        this.userViews = userViews;
    }

    public Set<String> getUserChecks() {
        return userChecks;
    }

    public void setUserChecks(Set<String> userChecks) {
        this.userChecks = userChecks;
    }

    public List<DocumentVersionEntity> getVersions() {
        return versions;
    }

    public void setVersions(List<DocumentVersionEntity> versions) {
        this.versions = versions;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

