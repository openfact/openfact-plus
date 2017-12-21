package org.clarksnut.documents;

import java.util.Date;

public interface Document {

    String getType();
    String getFileId();
    String getFileProvider();
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

    DocumentProviderType getProvider();
    boolean isVerified();

    Date getCreatedAt();
    Date getUpdatedAt();

}
