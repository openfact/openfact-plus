package org.openfact.models;

import org.openfact.provider.ProviderEvent;

import java.util.Date;
import java.util.List;
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

    interface DocumentCreationEvent extends ProviderEvent {
        String getDocumentType();
        Object getJaxb();
        DocumentModel getCreatedDocument();
    }

    interface DocumentRemovedEvent extends ProviderEvent {
        DocumentModel getDocument();
    }

}
