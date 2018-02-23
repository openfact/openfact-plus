package org.clarksnut.models;

import java.util.Date;
import java.util.List;

public interface DocumentModel {

    String getId();
    String getType();
    String getAssignedId();
    String getSupplierAssignedId();
    String getCustomerAssignedId();

    DocumentVersionModel getCurrentVersion();
    List<DocumentVersionModel> getVersions();

    IndexedDocumentModel getIndexedDocument();

    Date getCreatedAt();
    Date getUpdatedAt();

    interface DocumentCreationEvent {
        DocumentModel getCreatedDocument();
    }

    interface DocumentRemovedEvent {
        DocumentModel getDocument();
    }

}
