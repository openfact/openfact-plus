package org.openfact.models.db.jpa;

import org.openfact.models.*;
import org.openfact.models.db.jpa.entity.UserEntity;
import org.openfact.models.db.jpa.entity.UserRepositoryEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
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
    public String getIdentityID() {
        return user.getIdentityID();
    }

    @Override
    public String getProviderType() {
        return user.getProviderType();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public void setUsername(String username) {
        user.setUsername(username);
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
    public String getImageURL() {
        return user.getImageURL();
    }

    @Override
    public void setImageURL(String imageURL) {
        user.setImageURL(imageURL);
    }

    @Override
    public String getBio() {
        return user.getBio();
    }

    @Override
    public void setBio(String bio) {
        user.setBio(bio);
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public void setEmail(String email) {
        user.setEmail(email);
    }

    @Override
    public String getCompany() {
        return user.getCompany();
    }

    @Override
    public void setCompany(String company) {
        user.setCompany(company);
    }

    @Override
    public String getUrl() {
        return user.getUrl();
    }

    @Override
    public void setUrl(String url) {
        user.setUrl(url);
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return user.getCreatedAt();
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return user.getUpdatedAt();
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
