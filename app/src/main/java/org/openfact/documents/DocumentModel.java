package org.openfact.documents;

import java.util.Date;
import java.util.Map;

public interface DocumentModel {

    String ASSIGNED_ID = "assigned_id";

    String SUPPLIER_NAME = "supplier_name";
    String SUPPLIER_ASSIGNED_ID = "supplier_assigned_id";
    String CUSTOMER_ASSIGNED_ID = "customer_assigned_id";
    String CUSTOMER_NAME = "customer_name";

    String TYPE = "type";
    String CURRENCY = "currency";
    String ISSUE_DATE = "issue_date";
    String AMOUNT = "amount";

    String TAGS = "tags";

    String getId();

    String getType();

    String getAssignedId();

    String getFileId();

    Float getAmount();

    String getCurrency();

    Date getIssueDate();

    String getSupplierName();

    String getSupplierAssignedId();

    String getCustomerName();

    String getCustomerAssignedId();

    Map<String, String> getTags();

    Date getCreatedAt();

    Date getUpdatedAt();

    DocumentProviderType getProvider();

    interface DocumentCreationEvent {
        String getDocumentType();

        Object getJaxb();

        DocumentModel getCreatedDocument();
    }

    interface DocumentRemovedEvent {
        DocumentModel getDocument();
    }

}
