package org.clarksnut.documents.jpa.entity;

import org.clarksnut.common.jpa.CreatableEntity;
import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatableEntity;
import org.clarksnut.common.jpa.UpdatedAtListener;
import org.clarksnut.documents.DocumentProviderType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "cl_indexed_document")
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
public class IndexedDocumentEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull(message = "type should not be null")
    @Column(name = "type")
    private String type;

    @Column(name = "currency")
    private String currency;

    @NotNull(message = "provider should not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private DocumentProviderType provider;

    @Column(name = "supplier_name")
    private String supplierName;

    @NotNull(message = "supplierAssignedId should not be null")
    @Column(name = "supplier_assigned_id")
    private String supplierAssignedId;

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

    @NotNull(message = "assignedId should not be null")
    @Column(name = "assigned_id")
    private String assignedId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "issue_date")
    private Date issueDate;

    @Digits(integer = 10, fraction = 4, message = "amount has incorrect number of integer/fraction")
    @Type(type = "org.hibernate.type.FloatType")
    @Column(name = "amount")
    private Float amount;

    @Digits(integer = 10, fraction = 4, message = "tax has incorrect number of integer/fraction")
    @Type(type = "org.hibernate.type.FloatType")
    @Column(name = "tax")
    private Float tax;

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public DocumentProviderType getProvider() {
        return provider;
    }

    public void setProvider(DocumentProviderType provider) {
        this.provider = provider;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierAssignedId() {
        return supplierAssignedId;
    }

    public void setSupplierAssignedId(String supplierAssignedId) {
        this.supplierAssignedId = supplierAssignedId;
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

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
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
