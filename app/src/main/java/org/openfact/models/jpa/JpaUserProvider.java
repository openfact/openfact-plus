package org.openfact.models.jpa;

import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.models.jpa.entity.UserEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class JpaUserProvider implements UserProvider {

    @Inject
    private EntityManager em;

    @Override
    public UserModel getByUsername(String username) {
        TypedQuery<UserEntity> query = em.createNamedQuery("getUserByUsername", UserEntity.class);
        query.setParameter("username", username);
        List<UserEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new UserAdapter(em, entities.get(0));
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
