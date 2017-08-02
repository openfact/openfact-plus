package org.openfact.models.es.entity;

import java.util.Map;

public interface DocumentEntity {

    String getId();

    String getAssignedId();

    String getType();

    String getFileId();

    Map<String, Object> getAttributes();

}
