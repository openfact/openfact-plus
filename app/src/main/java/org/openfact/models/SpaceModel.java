package org.openfact.models;

import java.util.Set;

public interface SpaceModel {

    String getId();

    String getAlias();
    void setAlias(String alias);

    UserModel getOwner();
    void setOwner(UserModel user);

    Set<SharedSpaceModel> getSharedUsers();

    RequestAccessToSpaceModel requestAccess(UserModel user, Set<PermissionType> permissions);

}
