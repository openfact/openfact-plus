package org.clarksnut.models.jpa;

import org.clarksnut.models.QueryModel;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.SpaceProvider;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.jpa.entity.SpaceEntity;
import org.clarksnut.models.jpa.entity.UserEntity;
import org.clarksnut.models.utils.ClarksnutModelUtils;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class JpaSpaceProvider implements SpaceProvider {

    private final static String[] SEARCH_FIELDS = {"assignedId", "name"};

    @PersistenceContext
    private EntityManager em;

    @Override
    public SpaceModel addSpace(String assignedId, String name, UserModel owner) {
        UserEntity userEntity = UserAdapter.toEntity(owner, em);

        SpaceEntity entity = new SpaceEntity();
        entity.setId(ClarksnutModelUtils.generateId());
        entity.setAssignedId(assignedId);
        entity.setName(name);
        entity.setOwner(userEntity);
        em.persist(entity);
        em.flush();

        // Cache
        userEntity.getOwnedSpaces().add(entity);

        return new SpaceAdapter(em, entity);
    }

    @Override
    public SpaceModel getSpace(String id) {
        SpaceEntity entity = em.find(SpaceEntity.class, id);
        if (entity == null) return null;
        return new SpaceAdapter(em, entity);
    }

    @Override
    public SpaceModel getByAssignedId(String assignedId) {
        TypedQuery<SpaceEntity> query = em.createNamedQuery("getSpaceByAssignedId", SpaceEntity.class);
        query.setParameter("assignedId", assignedId);
        List<SpaceEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new SpaceAdapter(em, entities.get(0));
    }

    @Override
    public boolean removeSpace(SpaceModel space) {
        SpaceEntity entity = em.find(SpaceEntity.class, space.getId());
        if (entity == null) return false;
        em.remove(entity);
        em.flush();
        return true;
    }

    @Override
    public List<SpaceModel> getSpaces(UserModel user) {
        TypedQuery<SpaceEntity> query = em.createNamedQuery("getSpacesByUserId", SpaceEntity.class);
        query.setParameter("userId", user.getId());
        return query.getResultList().stream()
                .map(f -> new SpaceAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public List<SpaceModel> getSpaces(UserModel user, int offset, int limit) {
        TypedQuery<SpaceEntity> query = em.createNamedQuery("getSpacesByUserId", SpaceEntity.class);
        query.setParameter("userId", user.getId());

        if (offset != -1) query.setFirstResult(offset);
        if (limit != -1) query.setMaxResults(limit);

        return query.getResultList().stream()
                .map(f -> new SpaceAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public List<SpaceModel> getSpaces(QueryModel query) {
        TypedQuery<SpaceEntity> typedQuery = new JpaCriteria<>(em, SpaceEntity.class, SpaceEntity.class, query, SEARCH_FIELDS).buildTypedQuery();
        return typedQuery.getResultList().stream()
                .map(f -> new SpaceAdapter(em, f))
                .collect(Collectors.toList());
    }
}
