package org.openfact.models.jpa;

import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.models.jpa.entity.UserEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Stateless
public class JpaUserProvider implements UserProvider {

    @Inject
    private EntityManager em;

    @Override
    public UserModel getByUsername(String username) {
        return null;
    }

    @Override
    public UserModel addUser(String username) {
        UserEntity entity = new UserEntity();
        entity.setId(OpenfactModelUtils.generateId());
        entity.setUsername(username);
        em.persist(entity);
        return new UserAdapter(em, entity);
    }

}
