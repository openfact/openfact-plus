package org.openfact.models.jpa;

import org.hibernate.Hibernate;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.openfact.models.ModelDuplicateException;
import org.openfact.models.ScrollableResultsModel;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.models.jpa.entity.UserEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        entity.setRegistrationCompleted(false);

        try {

            em.persist(entity);
        } catch (ModelDuplicateException e) {
            System.out.println(e);
            throw e;
        }

        return new UserAdapter(em, entity);
    }

    @Override
    public List<UserModel> getUsers() {
        TypedQuery<UserEntity> query = em.createNamedQuery("getAllUsers", UserEntity.class);
        return query.getResultList().stream()
                .map(f -> new UserAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public ScrollableResultsModel<UserModel> getScrollableUsers() {
        Session session = em.unwrap(Session.class);
        ScrollableResults scrollableResults = session.createNamedQuery("getAllUsers").scroll(ScrollMode.FORWARD_ONLY);
        Function<UserEntity, UserModel> mapper = entity -> new UserAdapter(session, entity);
        ScrollableResultsAdapter<UserEntity, UserModel> result = new ScrollableResultsAdapter<>(scrollableResults, mapper);
        while (result.next()) {
            UserModel userModel = result.get();
            System.out.println(userModel.getUsername());
        }
        return result;
    }

}
