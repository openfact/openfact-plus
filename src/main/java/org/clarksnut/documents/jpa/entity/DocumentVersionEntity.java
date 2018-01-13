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

@Entity
@Table(name = "cl_document_version")
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
@NamedQueries({
        @NamedQuery(name = "getCurrentDocumentVersionByDocumentId", query = "select dv from DocumentVersionEntity dv inner join dv.document doc where doc.id=:documentId")
})
public class DocumentVersionEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull(message = "isCurrentVersion should not be null")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "is_current_version")
    private boolean isCurrentVersion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", foreignKey = @ForeignKey)
    private DocumentEntity document;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imported_file_id", foreignKey = @ForeignKey)
    private ImportedDocumentEntity importedFile;

    @Column(name = "currency")
    private String currency;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCurrentVersion() {
        return isCurrentVersion;
    }

    public void setCurrentVersion(boolean currentVersion) {
        isCurrentVersion = currentVersion;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public ImportedDocumentEntity getImportedFile() {
        return importedFile;
    }

    public void setImportedFile(ImportedDocumentEntity importedFile) {
        this.importedFile = importedFile;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

