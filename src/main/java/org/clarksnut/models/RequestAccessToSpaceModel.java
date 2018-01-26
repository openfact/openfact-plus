package org.clarksnut.models;

import java.util.Date;

public interface RequestAccessToSpaceModel {
    String getId();
    String getMessage();
    RequestAccessScope getScope();

    RequestStatusType getStatus();
    void setStatus(RequestStatusType status);

    Date getCreatedAt();
    Date getUpdatedAt();
}
