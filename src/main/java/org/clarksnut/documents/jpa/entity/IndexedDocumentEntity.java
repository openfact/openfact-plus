package org.clarksnut.documents.jpa.entity;

import org.clarksnut.common.jpa.CreatableEntity;
import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatableEntity;
import org.clarksnut.common.jpa.UpdatedAtListener;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cl_indexed_document")
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
@NamedQueries({
        @NamedQuery(name = "getIndexedDocumentById", query = "select d from IndexedDocumentEntity d where d.id=:documentId")
})
public class IndexedDocumentEntity implements CreatableEntity, UpdatableEntity, Serializable {

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

    @NotNull(message = "supplierAssignedId should not be null")
    @Column(name = "supplier_assigned_id")
    private String supplierAssignedId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "issue_date")
    private Date issueDate;

    @Column(name = "currency")
    private String currency;

    @Digits(integer = 10, fraction = 4, message = "amount has incorrect number of integer/fraction")
    @Type(type = "org.hibernate.type.FloatType")
    @Column(name = "amount")
    private Float amount;

    @Digits(integer = 10, fraction = 4, message = "tax has incorrect number of integer/fraction")
    @Type(type = "org.hibernate.type.FloatType")
    @Column(name = "tax")
    private Float tax;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "supplier_street_address")
    private String supplierStreetAddress;

    @Column(name = "supplier_city")
    private String supplierCity;

    @Column(name = "supplier_country")
    private String supplierCountry;


    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_assigned_id")
    private String customerAssignedId;

    @Column(name = "customer_street_address")
    private String customerStreetAddress;

    @Column(name = "customer_city")
    private String customerCity;

    @Column(name = "customer_country")
    private String customerCountry;


    @NotNull(message = "createdAt should not be null")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @NotNull(message = "updatedAt should not be null")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;


    @NotNull
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private DocumentEntity document;

    @ElementCollection
    @Column(name = "value")
    @CollectionTable(name = "user_starts", joinColumns = {@JoinColumn(name = "indexed_document_id")})
    private Set<String> userStarts = new HashSet<>();

    @ElementCollection
    @Column(name = "value")
    @CollectionTable(name = "user_views", joinColumns = {@JoinColumn(name = "indexed_document_id")})
    private Set<String> userViews = new HashSet<>();

    @ElementCollection
    @Column(name = "value")
    @CollectionTable(name = "user_checks", joinColumns = {@JoinColumn(name = "indexed_document_id")})
    private Set<String> userChecks = new HashSet<>();

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

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getTax() {
        return tax;
    }

    public void setTax(Float tax) {
        this.tax = tax;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAssignedId() {
        return customerAssignedId;
    }

    public void setCustomerAssignedId(String customerAssignedId) {
        this.customerAssignedId = customerAssignedId;
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

    /**
     * User interaction
     */
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

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }
}
