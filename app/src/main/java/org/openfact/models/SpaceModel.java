package org.openfact.models;

import java.util.Date;
import java.util.Set;

public interface SpaceModel {

    String getId();
    String getAssignedId();

    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);

    UserModel getOwner();

    Date getCreatedAt();
    Date getUpdatedAt();

//    Set<SharedSpaceModel> getSharedUsers();
//
//    RequestAccessToSpaceModel requestAccess(UserModel user, Set<PermissionType> permissions);
}
