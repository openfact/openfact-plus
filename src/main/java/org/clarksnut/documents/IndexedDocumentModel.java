package org.clarksnut.documents;

import java.util.Date;
import java.util.Set;

public interface IndexedDocumentModel {

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

    Date getIssueDate();
    String getCurrency();
    Float getTax();
    Float getAmount();
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

    /**
     * User interaction
     * */
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
}
