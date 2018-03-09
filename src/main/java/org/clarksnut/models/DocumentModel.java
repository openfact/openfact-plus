package org.clarksnut.models;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface DocumentModel {

    String[] FILTER_TEXT_FIELDS = {
            DocumentModel.ASSIGNED_ID,
            DocumentModel.SUPPLIER_NAME,
            DocumentModel.CUSTOMER_NAME,
            DocumentModel.SUPPLIER_ASSIGNED_ID,
            DocumentModel.CUSTOMER_ASSIGNED_ID
    };

    String ASSIGNED_ID = "assignedId";

    String SUPPLIER_NAME = "supplierName";
    String SUPPLIER_ASSIGNED_ID = "supplierAssignedId";
    String CUSTOMER_ASSIGNED_ID = "customerAssignedId";
    String CUSTOMER_NAME = "customerName";

    String TYPE = "type";
    String CURRENCY = "currency";
    String ISSUE_DATE = "issueDate";
    String AMOUNT = "amount";

    /**
     * */
    String STARRED = "starred";
    String VIEWED = "viewed";
    String CHECKED = "checked";
    String TAGS = "tags";

    String getId();

    String getType();

    String getAssignedId();

    String getSupplierAssignedId();

    String getCustomerAssignedId();

    Date getIssueDate();

    String getCurrency();

    Double getAmount();

    Double getTax();

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

    DocumentVersionModel getCurrentVersion();

    List<DocumentVersionModel> getVersions();

    /**
     * User interaction
     */
    Set<String> getUserStars();

    void setUserStarts(Set<String> userIds);

    void addStart(String userId);

    boolean removeStart(String userId);

    Set<String> getUserViews();

    void setUserViews(Set<String> userIds);

    void addViewed(String userId);

    boolean removeViewed(String userId);

    Set<String> getUserChecks();

    void setUserChecks(Set<String> userIds);

    void addCheck(String userId);

    boolean removeCheck(String userId);

    interface DocumentCreationEvent {
        DocumentModel getCreatedDocument();
    }

    interface DocumentRemovedEvent {
        DocumentModel getDocument();
    }

}
