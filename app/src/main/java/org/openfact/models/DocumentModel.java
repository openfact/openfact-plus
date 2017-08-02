package org.openfact.models;

import java.util.Map;

public interface DocumentModel {

    String getId();

    String getType();

    String getAssignedId();

    String getFileId();

    Map<String, Object> getAttributes();

}
