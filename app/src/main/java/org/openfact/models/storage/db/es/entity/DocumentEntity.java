package org.openfact.models.storage.db.es.entity;

import java.util.Map;

public interface DocumentEntity {

    String getId();

    String getAssignedId();

    String getType();

    String getFileId();

    Map<String, Object> getAttributes();

}
