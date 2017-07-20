package org.openfact.models.jpa;

import org.openfact.models.UserModel;
import org.openfact.models.jpa.entity.UserEntity;

import javax.persistence.EntityManager;

public class UserAdapter implements UserModel {

    private final EntityManager em;
    private final UserEntity entity;

    public UserAdapter(EntityManager em, UserEntity entity) {
        this.em = em;
        this.entity = entity;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getOfflineToken() {
        return null;
    }

    @Override
    public void setOfflineToken(String token) {

    }

    @Override
    public boolean isRegistrationCompleted() {
        return false;
    }

    @Override
    public void setRegistrationCompleted(Boolean registrationCompleted) {

    }
}
