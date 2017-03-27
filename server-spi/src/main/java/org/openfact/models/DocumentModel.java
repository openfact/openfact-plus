package org.openfact.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface DocumentModel {

    String ACCOUNTING_CUSTOMER_PARTY_ID = "accountingCustomerPartyId";
    String DOCUMENT_TYPE = "documentType";
    String DOCUMENT_CURRENCY_CODE = "documentCurrencyCode";
    String CUSTOMER_REGISTRATION_NAME = "customerRegistrationName";
    String CUSTOMER_ASSIGNED_ACCOUNT_ID = "customerAssignedAccountId";
    String ISSUE_DATE = "issueDate";

    String getId();

    String getDocumentId();
    String getDocumentType();
    AccountingCustomerPartyModel getCustomerParty();

    LocalDateTime getIssueDate();
    void setIssueDate(LocalDateTime issueDate);

    String getXmlFileId();
    void setXmlFileId(String xmlFileId);

    String getSupplierAssignedAccountId();
    void setSupplierAssignedAccountId(String supplierAssignedAccountId);

    String getSupplierAdditionalAcountId();
    void setSupplierAdditionalAccountId(String supplierAdditionalAccountId);

    /**
     * Attributes
     */
    void setAttribute(String name, String value);
    void setAttribute(String name, Integer value);
    void setAttribute(String name, Long value);
    void setAttribute(String name, BigDecimal value);
    void setAttribute(String name, Boolean value);

    void removeAttribute(String name);

    String getAttribute(String name, String defaultValue);
    Long getAttribute(String name, Long defaultValue);
    Integer getAttribute(String name, Integer defaultValue);
    BigDecimal getAttribute(String name, BigDecimal defaultValue);
    Boolean getAttribute(String name, Boolean defaultValue);

    Map<String, String> getAttributes();

    /**
     * Lines
     */
    DocumentLineModel addDocumentLine();
    List<DocumentLineModel> getDocumentLines();
    boolean removeDocumentLine(DocumentLineModel documentLine);

}
