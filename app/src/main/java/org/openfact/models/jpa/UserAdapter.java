package org.openfact.models.jpa;

import org.openfact.models.RequestStatus;
import org.openfact.models.SpaceModel;
import org.openfact.models.UserModel;
import org.openfact.models.jpa.entity.UserEntity;
import org.openfact.models.jpa.entity.UserSpaceEntity;

import javax.persistence.EntityManager;
import java.util.List;
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
            return ((UserAdapter) model).getUser();
        }
        return em.getReference(UserEntity.class, model.getId());
    }

    @Override
    public UserEntity getUser() {
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
    public String getOfflineToken() {
        return user.getOfflineToken();
    }

    @Override
    public void setOfflineToken(String token) {
        user.setOfflineToken(token);
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
    public List<SpaceModel> getOwnedSpaces() {
        return user.getOwnedSpaces().stream()
                .map(f -> new SpaceAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public List<SpaceModel> getMemberSpaces() {
        return user.getMemberSpaces().stream()
                .map(UserSpaceEntity::getSpace)
                .map(f -> new SpaceAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public List<SpaceModel> getMemberSpaces(RequestStatus requestStatus) {
        return user.getMemberSpaces().stream()
                .filter(p -> p.getStatus().equals(requestStatus))
                .map(UserSpaceEntity::getSpace)
                .map(f -> new SpaceAdapter(em, f))
                .collect(Collectors.toList());
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
