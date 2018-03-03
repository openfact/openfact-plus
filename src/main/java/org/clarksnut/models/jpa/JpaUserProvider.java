package org.clarksnut.models.jpa;

import org.clarksnut.models.QueryModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.models.jpa.entity.UserEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Stateless
public class JpaUserProvider implements UserProvider {

    private final static String[] SEARCH_FIELDS = {UserModel.USERNAME, UserModel.FULL_NAME};
    private final static Function<String, String> FIELD_MAPPER = modelFieldName -> {
        switch (modelFieldName) {
            case UserModel.USERNAME:
                return "username";
            case UserModel.FULL_NAME:
                return "fullName";
            default:
                return modelFieldName;
        }
    };

    @PersistenceContext
    private EntityManager em;

    @Override
    public UserModel addUser(String username, String providerType) {
        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID().toString());
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
    public List<UserModel> getUsers(QueryModel query) {
        TypedQuery<UserEntity> typedQuery = new JpaCriteria<>(em, UserEntity.class, UserEntity.class, query, SEARCH_FIELDS, FIELD_MAPPER)
                .buildTypedQuery();
        return typedQuery.getResultList().stream()
                .map(f -> new UserAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getUsers(String filterText) {
        return getUsers(filterText, -1, -1);
    }

    @Override
    public List<UserModel> getUsers(String filterText, int offset, int limit) {
        TypedQuery<UserEntity> query = em.createNamedQuery("searchUsersByFilterText", UserEntity.class);
        query.setParameter("filterText", "%" + filterText + "%");
        return query.getResultList().stream()
                .map(f -> new UserAdapter(em, f))
                .collect(Collectors.toList());
    }

}
