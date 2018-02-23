package org.clarksnut.models;

import java.util.Date;

public interface DocumentVersionModel {

    String getId();
    boolean isCurrentVersion();

    DocumentModel getDocument();
    ImportedDocumentModel getImportedDocument();

    Date getIssueDate();
    String getCurrency();
    Float getAmount();
    Float getTax();

    String getSupplierName();
    String getSupplierCity();
    String getSupplierStreetAddress();
    String getSupplierCountry();

    String getCustomerName();
    String getCustomerCity();
    String getCustomerStreetAddress();
    String getCustomerCountry();

    Date getCreatedAt();
    Date getUpdatedAt();

}
