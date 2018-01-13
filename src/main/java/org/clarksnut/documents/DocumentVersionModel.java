package org.clarksnut.documents;

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
    String getSupplierStreetAddress();
    String getSupplierCity();

    String getSupplierCountry();
    String getCustomerName();
    String getCustomerAssignedId();
    String getCustomerStreetAddress();
    String getCustomerCity();

    String getCustomerCountry();

    Date getCreatedAt();
    Date getUpdatedAt();

}
