package org.openfact.models.jpa;

import org.openfact.models.PermissionType;
import org.openfact.models.SharedSpaceModel;
import org.openfact.models.SpaceModel;
import org.openfact.models.UserModel;
import org.openfact.models.jpa.entity.SharedSpaceEntity;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SharedSpaceAdapter implements SharedSpaceModel {

    private final EntityManager em;
    private final SharedSpaceEntity sharedSpace;

    public SharedSpaceAdapter(EntityManager em, SharedSpaceEntity sharedSpace) {
        this.em = em;
        this.sharedSpace = sharedSpace;
    }

    @Override
    public UserModel getUser() {
        return new UserAdapter(em, sharedSpace.getUser());
    }

    @Override
    public SpaceModel getSpace() {
        return new SpaceAdapter(em, sharedSpace.getSpace());
    }

    @Override
    public Set<PermissionType> getPermissions() {
        return new HashSet<>(sharedSpace.getPermissions());
    }

}
