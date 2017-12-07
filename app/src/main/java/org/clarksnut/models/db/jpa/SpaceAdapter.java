package org.clarksnut.models.db.jpa;

import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.db.JpaModel;
import org.clarksnut.models.db.jpa.entity.CollaboratorEntity;
import org.clarksnut.models.db.jpa.entity.SpaceEntity;
import org.clarksnut.models.db.jpa.entity.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
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
    public String getName() {
        return space.getName();
    }

    @Override
    public void setName(String name) {
        space.setName(name);
    }

    @Override
    public String getDescription() {
        return space.getDescription();
    }

    @Override
    public void setDescription(String description) {
        space.setDescription(description);
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
    public Date getCreatedAt() {
        return space.getCreatedAt();
    }

    @Override
    public Date getUpdatedAt() {
        return space.getUpdatedAt();
    }

    @Override
    public List<UserModel> getCollaborators() {
        return space.getCollaborators().stream()
                .map(CollaboratorEntity::getUser)
                .map(f -> new UserAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getCollaborators(int offset, int limit) {
        TypedQuery<CollaboratorEntity> query = em.createNamedQuery("getCollaboratorsBySpaceId", CollaboratorEntity.class);
        query.setParameter("spaceId", space.getId());

        if (offset != -1) query.setFirstResult(offset);
        if (limit != -1) query.setMaxResults(limit);

        return query.getResultList().stream()
                .map(CollaboratorEntity::getUser)
                .map(f -> new UserAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public void addCollaborators(UserModel user) {
        UserEntity userEntity = UserAdapter.toEntity(user, em);

        CollaboratorEntity entity = new CollaboratorEntity(userEntity, space);
        em.persist(entity);

        // Cache
        space.getCollaborators().add(entity);
    }

    @Override
    public boolean removeCollaborators(UserModel user) {
        UserEntity userEntity = UserAdapter.toEntity(user, em);

        CollaboratorEntity entity = em.find(CollaboratorEntity.class, new CollaboratorEntity.Key(userEntity, space));
        if (entity == null) return false;
        em.remove(entity);
        return true;
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
