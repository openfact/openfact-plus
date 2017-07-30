package org.openfact.models.jpa;

import org.openfact.models.*;
import org.openfact.models.jpa.entity.RequestAccessToSpaceEntity;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Set;

public class RequestAccessToSpaceAdapter implements RequestAccessToSpaceModel {

    private final EntityManager em;
    private final RequestAccessToSpaceEntity requestAccessToSpace;

    public RequestAccessToSpaceAdapter(EntityManager em, RequestAccessToSpaceEntity requestAccessToSpace) {
        this.em = em;
        this.requestAccessToSpace = requestAccessToSpace;
    }

    @Override
    public UserModel getUser() {
        return new UserAdapter(em, requestAccessToSpace.getUser());
    }

    @Override
    public SpaceModel getSpace() {
        return new SpaceAdapter(em, requestAccessToSpace.getSpace());
    }

    @Override
    public Set<PermissionType> getPermissions() {
        return new HashSet<>(requestAccessToSpace.getPermissions());
    }

    @Override
    public String getMessage() {
        return requestAccessToSpace.getMessage();
    }

    @Override
    public RequestStatusType getStatus() {
        return requestAccessToSpace.getStatus();
    }

    @Override
    public void setStatus(RequestStatusType status) {
        requestAccessToSpace.setStatus(status);
    }

}
