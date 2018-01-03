package org.clarksnut.models.db.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserLinkedBrokerModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.db.jpa.entity.CollaboratorEntity;
import org.clarksnut.models.db.jpa.entity.SpaceEntity;
import org.clarksnut.models.db.jpa.entity.UserContextInformationEntity;
import org.clarksnut.models.db.jpa.entity.UserEntity;
import org.clarksnut.models.utils.JacksonUtil;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public String getEmailTheme() {
        return user.getEmailTheme();
    }

    @Override
    public void setEmailTheme(String emailTheme) {
        user.setEmailTheme(emailTheme);
    }

    @Override
    public String getLanguage() {
        return user.getLanguage();
    }

    @Override
    public void setLanguage(String language) {
        user.setLanguage(language);
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
        return user.getOwnedSpaces().stream()
                .map(f -> new SpaceAdapter(em, f))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<SpaceModel> getCollaboratedSpaces() {
        return user.getCollaboratedSpaces().stream()
                .map(f -> new SpaceAdapter(em, f.getSpace()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<SpaceModel> getAllPermitedSpaces() {
        Set<SpaceEntity> ownedSpaces = user.getOwnedSpaces();
        Set<CollaboratorEntity> collaboratedSpaces = user.getCollaboratedSpaces();
        return Stream.concat(ownedSpaces.stream(), collaboratedSpaces.stream().map(CollaboratorEntity::getSpace))
                .map(f -> new SpaceAdapter(em, f))
                .collect(Collectors.toSet());
    }

    @Override
    public JsonNode getContextInformation() {
        if (user.getContextInformation() != null) {
            return JacksonUtil.toJsonNode(user.getContextInformation().getValue());
        }
        return null;
    }

    @Override
    public void setContextInformation(JsonNode json) {
        UserContextInformationEntity contextInformationEntity = user.getContextInformation();
        if (contextInformationEntity == null) {
            contextInformationEntity = new UserContextInformationEntity();
            contextInformationEntity.setValue(json.textValue());
            contextInformationEntity.setUser(user);
            em.persist(contextInformationEntity);
        } else {
            contextInformationEntity.setValue(json.textValue());
            em.merge(contextInformationEntity);
            em.flush();
        }
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
    public List<UserLinkedBrokerModel> getLinkedBrokers() {
        return user.getLinkedBrokers().stream()
                .map(f -> new UserLinkedBrokerAdapter(em, this, f))
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
