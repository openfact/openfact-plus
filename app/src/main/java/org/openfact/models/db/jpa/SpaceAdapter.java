package org.openfact.models.db.jpa;

import org.openfact.models.*;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.db.jpa.entity.RequestAccessToSpaceEntity;
import org.openfact.models.db.jpa.entity.UserEntity;

import javax.persistence.EntityManager;
import java.util.Set;
import java.util.stream.Collectors;

public class SpaceAdapter implements SpaceModel, JpaModel<SpaceEntity> {

    private final EntityManager em;
    private final SpaceEntity space;

    public SpaceAdapter(EntityManager em, SpaceEntity entity) {
        this.em = em;
        this.space = entity;
    }

    public static SpaceEntity toEntity(SpaceModel model, EntityManager em) {
        if (model instanceof SpaceAdapter) {
            return ((SpaceAdapter) model).getEntity();
        }
        return em.getReference(SpaceEntity.class, model.getId());
    }

    @Override
    public SpaceEntity getEntity() {
        return space;
    }

    @Override
    public String getId() {
        return space.getId();
    }

    @Override
    public String getAssignedId() {
        return space.getAssignedId();
    }

    @Override
    public String getAlias() {
        return space.getAlias();
    }

    @Override
    public void setAlias(String alias) {
        space.setAlias(alias);
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
    public Set<SharedSpaceModel> getSharedUsers() {
        return space.getSharedUsers().stream()
                .map(f -> new SharedSpaceAdapter(em, f))
                .collect(Collectors.toSet());
    }

    @Override
    public RequestAccessToSpaceModel requestAccess(UserModel user, Set<PermissionType> permissions) {
        UserEntity userEntity = UserAdapter.toEntity(user, em);

        RequestAccessToSpaceEntity entity = new RequestAccessToSpaceEntity(userEntity, space);
        entity.setSpace(space);
        entity.setUser(userEntity);
        entity.setPermissions(permissions);
        entity.setStatus(RequestStatusType.REQUESTED);

        // Cache
        space.getAccessRequests().add(entity);
        userEntity.getSpaceRequests().add(entity);

        return new RequestAccessToSpaceAdapter(em, entity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SpaceModel)) {
            return false;
        }
        SpaceModel other = (SpaceModel) obj;
        if (getId() != null) {
            if (!getId().equals(other.getId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

}
