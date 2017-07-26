package org.openfact.models.jpa;

import org.openfact.models.UserModel;
import org.openfact.models.jpa.entity.UserEntity;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserAdapter implements UserModel {

    private final EntityManager em;
    private final UserEntity entity;

    public UserAdapter(EntityManager em, UserEntity entity) {
        this.em = em;
        this.entity = entity;
    }

    @Override
    public String getId() {
        return entity.getId();
    }

    @Override
    public String getUsername() {
        return entity.getUsername();
    }

    @Override
    public String getOfflineToken() {
        return entity.getOfflineToken();
    }

    @Override
    public void setOfflineToken(String token) {
        entity.setOfflineToken(token);
    }

    @Override
    public boolean isRegistrationCompleted() {
        return entity.isRegistrationCompleted();
    }

    @Override
    public void setRegistrationCompleted(boolean registrationCompleted) {
        entity.setRegistrationCompleted(registrationCompleted);
    }

    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public void setFullName(String fullName) {

    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (getId() != null)
            result += "id: " + getId();
        return result;
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
