package org.clarksnut.documents;

import org.clarksnut.models.SpaceModel;

import java.util.Date;
import java.util.List;

public interface DocumentModel {

    String getId();
    String getType();
    String getAssignedId();

    SpaceModel getSupplier();
    SpaceModel getCustomer();

    List<DocumentVersionModel> getVersions();
    DocumentVersionModel getCurrentVersion();

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
