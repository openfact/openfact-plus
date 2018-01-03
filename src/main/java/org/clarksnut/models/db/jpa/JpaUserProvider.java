package org.clarksnut.models.db.jpa;

import org.clarksnut.common.jpa.HibernateProvider;
import org.clarksnut.models.QueryModel;
import org.clarksnut.models.UserBean;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.models.db.jpa.entity.UserEntity;
import org.clarksnut.models.exceptions.ModelException;
import org.clarksnut.models.utils.ClarksnutModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class JpaUserProvider extends HibernateProvider implements UserProvider {

    private final static String[] SEARCH_FIELDS = {"username", "fullName"};

    private EntityManager em;

    @Inject
    public JpaUserProvider(EntityManager em) {
        this.em = em;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public UserModel addUser(String identityID, String providerType, String username) {
        UserEntity entity = new UserEntity();
        entity.setId(ClarksnutModelUtils.generateId());
        entity.setIdentityID(identityID);
        entity.setProviderType(providerType);
        entity.setUsername(username);
        entity.setRegistrationCompleted(false);
        em.persist(entity);
        return new UserAdapter(em, entity);
    }

    @Override
    public UserModel getUser(String userId) {
        UserEntity entity = em.find(UserEntity.class, userId);
        if (entity == null) return null;
        return new UserAdapter(em, entity);
    }

    @Override
    public UserModel getUserByUsername(String username) {
        TypedQuery<UserEntity> query = em.createNamedQuery("getUserByUsername", UserEntity.class);
        query.setParameter("username", username);
        List<UserEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new UserAdapter(em, entities.get(0));
    }

    @Override
    public UserModel getUserByIdentityID(String identityID) {
        TypedQuery<UserEntity> query = em.createNamedQuery("getUserByIdentityID", UserEntity.class);
        query.setParameter("identityID", identityID);
        List<UserEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new UserAdapter(em, entities.get(0));
    }

    @Override
    public List<UserModel> getUsers(QueryModel query) {
        TypedQuery<UserEntity> typedQuery = new JpaCriteria<>(em, UserEntity.class, UserEntity.class, query, SEARCH_FIELDS)
                .buildTypedQuery();
        return typedQuery.getResultList().stream()
                .map(f -> new UserAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getUsersWithOfflineToken() {
        return getUsersWithOfflineToken(-1, -1);
    }

    @Override
    public List<UserModel> getUsersWithOfflineToken(int offset, int limit) {
        TypedQuery<UserEntity> query = em.createNamedQuery("getUserWithOfflineToken", UserEntity.class);
        if (offset != -1) {
            query.setFirstResult(offset);
        }
        if (limit != -1) {
            query.setMaxResults(limit);
        }

        return query.getResultList().stream()
                .map(f -> new UserAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public void updateUser(UserBean user) {
        UserEntity userEntity = null;
        if (user.getId() != null) {
            userEntity = em.find(UserEntity.class, user.getId());
        } else if (user.getIdentityID() != null) {
            TypedQuery<UserEntity> query = em.createNamedQuery("getUserByIdentityID", UserEntity.class);
            query.setParameter("identityID", user.getIdentityID());
            List<UserEntity> entities = query.getResultList();
            if (entities.size() == 1) {
                userEntity = entities.get(0);
            } else if (entities.size() > 1) {
                throw new ModelException("Inconsistent data, found more than one user with identityID=" + user.getIdentityID());
            }
        } else {
            throw new ModelException("Could not identity user identity, please add id or identityID information");
        }

        if (userEntity == null) {
            throw new ModelException("User not found");
        }

        if (user.getOfflineToken() != null) {
            userEntity.setOfflineToken(user.getOfflineToken());
        }
        if (user.getRegistrationComplete() != null) {
            userEntity.setRegistrationCompleted(user.getRegistrationComplete());
        }

        em.merge(userEntity);
    }

}
