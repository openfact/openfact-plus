package org.openfact.models.db.jpa;

import org.openfact.models.SpaceModel;
import org.openfact.models.SpaceProvider;
import org.openfact.models.UserModel;
import org.openfact.models.db.HibernateProvider;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.db.jpa.entity.UserEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class JpaSpaceProvider extends HibernateProvider implements SpaceProvider {

    private EntityManager em;

    @Inject
    public JpaSpaceProvider(EntityManager em) {
        this.em = em;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SpaceModel addSpace(String assignedId, UserModel owner) {
        UserEntity userEntity = UserAdapter.toEntity(owner, getSession());

        SpaceEntity entity = new SpaceEntity();
        entity.setId(OpenfactModelUtils.generateId());
        entity.setAssignedId(assignedId);
        entity.setAlias(assignedId);
        entity.setOwner(userEntity);
        getSession().persist(entity);

        getSession().flush();

        // Cache
        userEntity.getOwnedSpaces().add(entity);

        return new SpaceAdapter(getSession(), entity);
    }

    @Override
    public SpaceModel getByAssignedId(String assignedId) {
        TypedQuery<SpaceEntity> query = getSession().createNamedQuery("getSpaceByAssignedId", SpaceEntity.class);
        query.setParameter("assignedId", assignedId);
        List<SpaceEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return new SpaceAdapter(getSession(), entities.get(0));
    }

    @Override
    public boolean removeSpace(SpaceModel space) {
        SpaceEntity entity = getSession().find(SpaceEntity.class, space.getId());
        if (entity == null) return false;
        getSession().remove(entity);
        getSession().flush();
        return true;
    }
}
