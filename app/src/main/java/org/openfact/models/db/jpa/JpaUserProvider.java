package org.openfact.models.db.jpa;

import org.openfact.models.ScrollableResultsModel;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.models.QueryModel;
import org.openfact.models.db.HibernateProvider;
import org.openfact.models.db.jpa.entity.UserEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Stateless
public class JpaUserProvider extends HibernateProvider implements UserProvider {

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
        entity.setId(OpenfactModelUtils.generateId());
        entity.setIdentityID(identityID);
        entity.setProviderType(providerType);
        entity.setUsername(username);
        entity.setRegistrationCompleted(false);
        em.persist(entity);
        em.flush();
        return new UserAdapter(em, entity);
    }

    @Override
    public UserModel getUser(String userId) {
        UserEntity entity = em.find(UserEntity.class, userId);
        if (entity == null) return null;
        return new UserAdapter(em, entity);
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
    public List<UserModel> getUsers() {
        TypedQuery<UserEntity> query = em.createNamedQuery("getAllUsers", UserEntity.class);
        return query.getResultList().stream()
                .map(f -> new UserAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getUsers(QueryModel query) {
        TypedQuery<UserEntity> typedQuery = new JpaCriteria<>(em, UserEntity.class, UserEntity.class, query).buildTypedQuery();
        return typedQuery.getResultList().stream()
                .map(f -> new UserAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public ScrollableResultsModel<UserModel> getScrollableUsers() {
//        ScrollableResults scrollableResults = getSession().createNamedQuery("getAllUsers").scroll(ScrollMode.FORWARD_ONLY);
//        Function<UserEntity, UserModel> mapper = entity -> new UserAdapter(em, entity);
//        return new ScrollableResultsAdapter<>(scrollableResults, mapper);
        return null;
    }

}
