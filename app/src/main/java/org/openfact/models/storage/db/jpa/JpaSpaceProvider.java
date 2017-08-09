package org.openfact.models.storage.db.jpa;

import org.openfact.models.SpaceModel;
import org.openfact.models.SpaceProvider;
import org.openfact.models.UserModel;
import org.openfact.models.storage.db.jpa.entity.SpaceEntity;
import org.openfact.models.storage.db.jpa.entity.UserEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class JpaSpaceProvider implements SpaceProvider {

    @Inject
    private EntityManager em;

    @Override
    public SpaceModel addSpace(String assignedId, UserModel owner) {
        UserEntity userEntity = UserAdapter.toEntity(owner, em);

        SpaceEntity entity = new SpaceEntity();
        entity.setId(OpenfactModelUtils.generateId());
        entity.setAssignedId(assignedId);
        entity.setOwner(userEntity);
        em.persist(entity);

        // Cache
        userEntity.getOwnedSpaces().add(entity);

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

}
