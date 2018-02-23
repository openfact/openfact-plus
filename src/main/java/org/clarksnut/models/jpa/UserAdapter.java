package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.PermissionType;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.jpa.entity.CollaboratorEntity;
import org.clarksnut.models.jpa.entity.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.HashSet;
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
    public String getProviderType() {
        return user.getProviderType();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
    public String getDefaultLanguage() {
        return user.getDefaultLanguage();
    }

    @Override
    public void setDefaultLanguage(String language) {
        user.setDefaultLanguage(language);
    }

    @Override
    public Date getCreatedAt() {
        return user.getCreatedAt();
    }

    @Override
    public Date getUpdatedAt() {
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
        TypedQuery<CollaboratorEntity> query = em.createNamedQuery("getCollaboratorsByUserIdAndRole", CollaboratorEntity.class);
        query.setParameter("userId", user.getId());
        query.setParameter("role", PermissionType.OWNER);

        return query.getResultList().stream()
                .map(CollaboratorEntity::getSpace)
                .map(space -> new SpaceAdapter(em, space))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<SpaceModel> getCollaboratedSpaces() {
        TypedQuery<CollaboratorEntity> query = em.createNamedQuery("getCollaboratorsByUserIdAndRole", CollaboratorEntity.class);
        query.setParameter("userId", user.getId());
        query.setParameter("role", PermissionType.COLLABORATOR);

        return query.getResultList().stream()
                .map(CollaboratorEntity::getSpace)
                .map(space -> new SpaceAdapter(em, space))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<SpaceModel> getAllPermitedSpaces() {
        TypedQuery<CollaboratorEntity> query = em.createNamedQuery("getCollaboratorsByUserId", CollaboratorEntity.class);
        query.setParameter("userId", user.getId());

        return query.getResultList().stream()
                .map(CollaboratorEntity::getSpace)
                .map(space -> new SpaceAdapter(em, space))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getFavoriteSpaces() {
        Set<String> result = new HashSet<>();
        result.addAll(user.getFavoriteSpaces());
        return result;
    }

    @Override
    public void setFavoriteSpaces(Set<String> spaces) {
        user.setFavoriteSpaces(spaces);
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
