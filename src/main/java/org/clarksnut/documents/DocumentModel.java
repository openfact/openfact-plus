package org.clarksnut.documents;

import java.util.List;

public interface DocumentModel extends Document {

    String ASSIGNED_ID = "assignedId";

    String SUPPLIER_NAME = "supplierName";
    String SUPPLIER_ASSIGNED_ID = "supplierAssignedId";
    String CUSTOMER_ASSIGNED_ID = "customerAssignedId";
    String CUSTOMER_NAME = "customerName";

    String TYPE = "type";
    String CURRENCY = "currency";
    String ISSUE_DATE = "issueDate";
    String AMOUNT = "amount";

    String getId();

    boolean hasChanged();

    List<DocumentVersionModel> getVersions();

    interface DocumentCreationEvent {
        String getDocumentType();

        Object getJaxb();

        DocumentModel getCreatedDocument();
    }

    interface DocumentRemovedEvent {
        DocumentModel getDocument();
    }

}
