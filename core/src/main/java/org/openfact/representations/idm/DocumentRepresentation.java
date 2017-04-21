package org.openfact.representations.idm;

import java.util.Date;
import java.util.Map;

public class DocumentRepresentation {

    private String id;
    private String documentId;
    private String documentType;
    private Date issueDate;
    private String  documentCurrencyCode;
    private String supplierAssignedAccountId;
    private String customerAssignedAccountId;

    private Map<String, Object> attributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getDocumentCurrencyCode() {
        return documentCurrencyCode;
    }

    public void setDocumentCurrencyCode(String documentCurrencyCode) {
        this.documentCurrencyCode = documentCurrencyCode;
    }

    public String getSupplierAssignedAccountId() {
        return supplierAssignedAccountId;
    }

    public void setSupplierAssignedAccountId(String supplierAssignedAccountId) {
        this.supplierAssignedAccountId = supplierAssignedAccountId;
    }

    public String getCustomerAssignedAccountId() {
        return customerAssignedAccountId;
    }

    public void setCustomerAssignedAccountId(String customerAssignedAccountId) {
        this.customerAssignedAccountId = customerAssignedAccountId;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
