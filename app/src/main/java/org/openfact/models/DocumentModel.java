package org.openfact.models;

import org.openfact.provider.ProviderEvent;

import java.util.Date;
import java.util.Map;

public interface DocumentModel {

    String getId();

    String getType();

    String getAssignedId();

    String getFileId();

    Map<String, String> getTags();

    Date getCreatedAt();
    Date getUpdatedAt();

    SpaceModel getSpace();

    interface DocumentCreationEvent extends ProviderEvent {
        String getType();
        Object getDocumentType();
        DocumentModel getCreatedDocument();
    }

    interface DocumentRemovedEvent extends ProviderEvent {
        DocumentModel getDocument();
    }

}
