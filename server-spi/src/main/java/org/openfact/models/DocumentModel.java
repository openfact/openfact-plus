package org.openfact.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface DocumentModel {

    String DOCUMENT_ID = "documentId";
    String DOCUMENT_TYPE = "documentType";
    String DOCUMENT_CURRENCY_CODE = "documentCurrencyCode";
    String ISSUE_DATE = "issueDate";

    String getId();

    String getDocumentId();
    String getDocumentType();
    String getOriginUuid();

    LocalDateTime getIssueDate();
    void setIssueDate(LocalDateTime issueDate);

    String getDocumentCurrencyCode();
    void setDocumentCurrencyCode(String documentCurrencyCode);

    String getSupplierAssignedAccountId();
    void setSupplierAssignedAccountId(String supplierAssignedAccountId);

    String getCustomerAssignedAccountId();
    void setCustomerAssignedAccountId(String customerAssignedAccountId);

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
    Map<String, Object> getAttributesFormated();

    /**
     * Lines
     */
    DocumentLineModel addDocumentLine();
    List<DocumentLineModel> getDocumentLines();
    boolean removeDocumentLine(DocumentLineModel documentLine);

}
