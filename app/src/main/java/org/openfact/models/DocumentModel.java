package org.openfact.models;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface DocumentModel {

    String getId();

    String getType();

    String getAssignedId();

    String getFileId();

    Map<String, String> getTags();

    Date getCreatedAt();
    Date getUpdatedAt();

    Set<SpaceModel> getSpaces();

    interface DocumentCreationEvent {
        String getDocumentType();
        Object getJaxb();
        DocumentModel getCreatedDocument();
    }

    interface DocumentRemovedEvent {
        DocumentModel getDocument();
    }

}
