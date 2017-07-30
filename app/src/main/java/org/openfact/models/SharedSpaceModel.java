package org.openfact.models;

import java.util.Set;

public interface SharedSpaceModel {

    UserModel getUser();

    SpaceModel getSpace();

    Set<PermissionType> getPermissions();

}
