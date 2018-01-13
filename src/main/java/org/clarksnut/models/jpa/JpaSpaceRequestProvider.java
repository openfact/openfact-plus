package org.clarksnut.models.jpa;

import org.clarksnut.files.FileModel;
import org.clarksnut.models.*;
import org.clarksnut.models.jpa.entity.SpaceEntity;
import org.clarksnut.models.jpa.entity.SpaceRequestEntity;
import org.clarksnut.models.jpa.entity.UserEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
public class JpaSpaceRequestProvider implements SpaceRequestProvider {

    private final static String[] SEARCH_FIELDS = {"assignedId", "name"};

    @PersistenceContext
    private EntityManager em;

    @Override
    public SpaceRequestModel addRequest(SpaceModel space, UserModel user, FileModel userPhotograph, String message, RequestType type) {
        SpaceEntity spaceEntity = SpaceAdapter.toEntity(space, em);
        UserEntity userEntity = UserAdapter.toEntity(user, em);

        SpaceRequestEntity entity = new SpaceRequestEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setType(type);
        entity.setStatus(RequestStatusType.PENDING);
        entity.setFileId(userPhotograph.getId());
        entity.setSpace(spaceEntity);
        entity.setUser(userEntity);
        em.persist(entity);

        return new SpaceRequestAdapter(em, entity);
    }

    @Override
    public SpaceRequestModel getRequest(String id) {
        SpaceRequestEntity entity = em.find(SpaceRequestEntity.class, id);
        if (entity == null) return null;
        return new SpaceRequestAdapter(em, entity);
    }

    @Override
    public List<SpaceRequestModel> getRequests(SpaceModel space) {
        return getRequests(space, -1, -1);
    }

    @Override
    public List<SpaceRequestModel> getRequests(SpaceModel space, int offset, int limit) {
        TypedQuery<SpaceRequestEntity> query = em.createNamedQuery("getSpaceRequestBySpaceId", SpaceRequestEntity.class);
        query.setParameter("spaceId", space.getId());

        if (offset != -1) query.setFirstResult(offset);
        if (limit != -1) query.setMaxResults(limit);

        return query.getResultList().stream()
                .map(f -> new SpaceRequestAdapter(em, f))
                .collect(Collectors.toList());
    }

}
