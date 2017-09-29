package org.openfact.models.db.es.reader.basic.invoice;

import org.hibernate.search.annotations.Indexed;
import org.openfact.models.db.es.entity.DocumentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Indexed
@Table(name = "invoice")
@NamedQueries({
        @NamedQuery(name = "getInvoiceByDocumentId", query = "select d from PEInvoiceEntity d inner join d.document u where u.id = :documentId")
})
public class BasicInvoiceEntity {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubl_document_id", foreignKey = @ForeignKey)
    private DocumentEntity document;

    @Column(name = "assigned_id")
    private String assignedId;

    @Temporal(TemporalType.DATE)
    @Column(name = "issue_date")
    private Date issueDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "issue_time")
    private Date issueTime;

    @Column(name = "type_code")
    private String typeCode;

    @Column(name = "currency_code")
    private String currencyCode;

    /*
     * Supplier Party
     * */
    @Column(name = "supplier_assigned_account_id")
    private String supplierAssignedAccountId;

    @Column(name = "supplier_registration_name")
    private String supplierRegistrationName;

    /*
     * Customer Party
     * */
    @Column(name = "customer_assigned_account_id")
    private String customerAssignedAccountId;

    @Column(name = "customer_additional_account_id")
    private String customerAdditionalAccountId;

    /*
     * Legal Monetary Total
     * */
    @Column(name = "payable_amount")
    private BigDecimal payableAmount;

    /*
     * Lines
     * */
    @OneToMany(cascade = {CascadeType.REMOVE}, orphanRemoval = true, mappedBy = "invoice", fetch = FetchType.LAZY)
    private List<BasicInvoiceLineEntity> lines = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getSupplierAssignedAccountId() {
        return supplierAssignedAccountId;
    }

    public void setSupplierAssignedAccountId(String supplierAssignedAccountId) {
        this.supplierAssignedAccountId = supplierAssignedAccountId;
    }

    public String getSupplierRegistrationName() {
        return supplierRegistrationName;
    }

    public void setSupplierRegistrationName(String supplierRegistrationName) {
        this.supplierRegistrationName = supplierRegistrationName;
    }

    public String getCustomerAssignedAccountId() {
        return customerAssignedAccountId;
    }

    public void setCustomerAssignedAccountId(String customerAssignedAccountId) {
        this.customerAssignedAccountId = customerAssignedAccountId;
    }

    public String getCustomerAdditionalAccountId() {
        return customerAdditionalAccountId;
    }

    public void setCustomerAdditionalAccountId(String customerAdditionalAccountId) {
        this.customerAdditionalAccountId = customerAdditionalAccountId;
    }

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public List<BasicInvoiceLineEntity> getLines() {
        return lines;
    }

    public void setLines(List<BasicInvoiceLineEntity> lines) {
        this.lines = lines;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }
}
