package org.openfact.models.storage.db.jpa;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.openfact.models.ScrollableResultsModel;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.models.storage.db.HibernateProvider;
import org.openfact.models.storage.db.jpa.entity.UserEntity;
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
    public UserModel getByUsername(String username) {
        TypedQuery<UserEntity> query = getSession().createNamedQuery("getUserByUsername", UserEntity.class);
        query.setParameter("username", username);
        List<UserEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new UserAdapter(getSession(), entities.get(0));
    }

    @Override
    public UserModel addUser(String username) {
        UserEntity entity = new UserEntity();
        entity.setId(OpenfactModelUtils.generateId());
        entity.setUsername(username);
        entity.setRegistrationCompleted(false);
        getSession().persist(entity);
        return new UserAdapter(getSession(), entity);
    }

    @Override
    public List<UserModel> getUsers() {
        TypedQuery<UserEntity> query = getSession().createNamedQuery("getAllUsers", UserEntity.class);
        return query.getResultList().stream()
                .map(f -> new UserAdapter(getSession(), f))
                .collect(Collectors.toList());
    }

    @Override
    public ScrollableResultsModel<UserModel> getScrollableUsers() {
        ScrollableResults scrollableResults = getSession().createNamedQuery("getAllUsers").scroll(ScrollMode.FORWARD_ONLY);
        Function<UserEntity, UserModel> mapper = entity -> new UserAdapter(getSession(), entity);
        return new ScrollableResultsAdapter<>(scrollableResults, mapper);
    }

}
