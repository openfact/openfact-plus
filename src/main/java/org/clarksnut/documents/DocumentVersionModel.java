package org.clarksnut.documents;

import java.util.Date;

public interface DocumentVersionModel {

    String getId();
    boolean isCurrentVersion();
    DocumentProviderType getProvider();

    DocumentModel getDocument();

    ImportedDocumentModel getImportedDocument();
    String getType();
    String getFileId();
    String getAssignedId();

    Date getIssueDate();
    String getCurrency();
    Float getAmount();

    Float getTax();
    String getSupplierName();
    String getSupplierAssignedId();
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
