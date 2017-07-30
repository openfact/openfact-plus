package org.openfact.models;

import java.util.Set;

public interface RequestAccessToSpaceModel {

    UserModel getUser();
    SpaceModel getSpace();

    Set<PermissionType> getPermissions();
    String getMessage();

    RequestStatusType getStatus();
    void setStatus(RequestStatusType type);

}
