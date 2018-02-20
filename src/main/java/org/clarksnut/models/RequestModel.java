package org.clarksnut.models;

import java.util.Date;

public interface RequestModel {
    String getId();
    String getMessage();
    RequestScope getScope();

    RequestStatus getStatus();
    void setStatus(RequestStatus status);

    Date getCreatedAt();
    Date getUpdatedAt();

    SpaceModel getSpace();
    UserModel getUser();
}
