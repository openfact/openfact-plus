package org.openfact.models.db.jpa;

import org.openfact.models.BrokerType;
import org.openfact.models.*;
import org.openfact.models.db.jpa.entity.UserRepositoryEntity;
import org.openfact.models.db.jpa.entity.UserEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserAdapter implements UserModel, JpaModel<UserEntity> {

    private final EntityManager em;
    private final UserEntity user;

    public UserAdapter(EntityManager em, UserEntity user) {
        this.em = em;
        this.user = user;
    }

    public static UserEntity toEntity(UserModel model, EntityManager em) {
        if (model instanceof UserAdapter) {
            return ((UserAdapter) model).getEntity();
        }
        return em.getReference(UserEntity.class, model.getId());
    }

    @Override
    public UserEntity getEntity() {
        return user;
    }

    @Override
    public String getId() {
        return user.getId();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getOfflineRefreshToken() {
        return user.getOfflineToken();
    }

    @Override
    public void setOfflineRefreshToken(String refreshToken) {
        user.setOfflineToken(refreshToken);
    }

    @Override
    public boolean isRegistrationCompleted() {
        return user.isRegistrationCompleted();
    }

    @Override
    public void setRegistrationCompleted(boolean registrationCompleted) {
        user.setRegistrationCompleted(registrationCompleted);
    }

    @Override
    public String getFullName() {
        return user.getFullName();
    }

    @Override
    public void setFullName(String fullName) {
        user.setFullName(fullName);
    }

    @Override
    public Set<SpaceModel> getOwnedSpaces() {
        return user.getOwnedSpaces().stream()
                .map(f -> new SpaceAdapter(em, f))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<SharedSpaceModel> getSharedSpaces() {
        return user.getSharedSpaces().stream()
                .map(f -> new SharedSpaceAdapter(em, f))
                .collect(Collectors.toSet());
    }

    @Override
    public List<RequestAccessToSpaceModel> getSpaceRequests() {
        return user.getSpaceRequests().stream()
                .map(f -> new RequestAccessToSpaceAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public UserRepositoryModel addRepository(String email, BrokerType type) {
        UserRepositoryEntity entity = new UserRepositoryEntity();
        entity.setId(OpenfactModelUtils.generateId());
        entity.setAlias(email);
        entity.setEmail(email);
        entity.setType(type);
        entity.setUser(user);
        em.persist(entity);

        // Cache
        user.getRepositories().add(entity);

        return new UserRepositoryAdapter(em, entity);
    }

    @Override
    public List<UserRepositoryModel> getRepositories() {
        return user.getRepositories().stream()
                .map(f -> new UserRepositoryAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public boolean removeAllRepositories() {
        user.setRepositories(new HashSet<>());
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UserModel)) {
            return false;
        }
        UserModel other = (UserModel) obj;
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
