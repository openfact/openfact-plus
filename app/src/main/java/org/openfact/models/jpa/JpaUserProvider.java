package org.openfact.models.jpa;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.openfact.models.ScrollableResultsModel;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.models.jpa.entity.UserEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.function.Function;

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
        em.persist(entity);
        return new UserAdapter(em, entity);
    }

    @Override
    public ScrollableResultsModel<UserModel> getUsers() {
        Session session = em.unwrap(Session.class);
        StatelessSession statelessSession = session.getSessionFactory().openStatelessSession();
        ScrollableResults scroll = statelessSession.createNamedQuery("getAllUsers").scroll(ScrollMode.FORWARD_ONLY);

        Function<UserEntity, UserModel> mapper = entity -> new UserAdapter(em, entity);
        return new ScrollableResultsAdapter<UserEntity, UserModel>(session, statelessSession, scroll, mapper);
    }

}
