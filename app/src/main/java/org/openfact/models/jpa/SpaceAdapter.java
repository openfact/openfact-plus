package org.openfact.models.jpa;

import org.openfact.models.RequestStatus;
import org.openfact.models.SpaceModel;
import org.openfact.models.UserModel;
import org.openfact.models.jpa.entity.SpaceEntity;
import org.openfact.models.jpa.entity.UserEntity;
import org.openfact.models.jpa.entity.UserSpaceEntity;
import org.openfact.models.jpa.entity.UserSpaceEntity.UserSpaceIdEntity;

import javax.persistence.EntityManager;

public class SpaceAdapter implements SpaceModel {

    private final EntityManager em;
    private final SpaceEntity space;

    public SpaceAdapter(EntityManager em, SpaceEntity entity) {
        this.em = em;
        this.space = entity;
    }

    @Override
    public String getId() {
        return space.getId();
    }

    @Override
    public String getAlias() {
        return space.getAlias();
    }

    @Override
    public UserModel getOwner() {
        UserEntity owner = space.getOwner();
        return owner != null ? new UserAdapter(em, owner) : null;
    }

    @Override
    public void setOwner(UserModel user) {
        UserEntity userEntity = UserAdapter.toEntity(user, em);
        space.setOwner(userEntity);
    }

    @Override
    public RequestStatus requestMemberApproval(UserModel user) {
        RequestStatus requestStatus = RequestStatus.REQUESTED;
        UserEntity userEntity = UserAdapter.toEntity(user, em);

        // Persist
        UserSpaceEntity userSpaceEntity = new UserSpaceEntity(new UserSpaceIdEntity(userEntity, space));
        userSpaceEntity.setStatus(requestStatus);
        em.persist(userSpaceEntity);

        // Update cache
        /*userEntity.getMemberSpaces().add(userSpaceEntity);
        space.getMembers().add(userSpaceEntity);*/

        return requestStatus;
    }

}
