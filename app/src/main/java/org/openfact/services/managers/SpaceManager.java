package org.openfact.services.managers;

import org.openfact.models.PermissionType;
import org.openfact.models.SpaceModel;
import org.openfact.models.SpaceProvider;
import org.openfact.models.UserModel;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@Stateless
public class SpaceManager {

    @Inject
    private SpaceProvider spaceProvider;

//    public void claimAccountId(String claimedAccountId, UserModel user) {
//        SpaceModel space = spaceProvider.getByAssignedId(claimedAccountId);
//        if (space == null) {
//            spaceProvider.addSpace(claimedAccountId, user);
//        } else {
//            Set<PermissionType> permissions = new HashSet<>();
//            permissions.add(PermissionType.READ);
//
//            space.requestAccess(user, permissions);
//        }
//    }

}
