package org.clarksnut.models.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.models.PartyModel;
import org.clarksnut.models.jpa.entity.PartyEntity;

import javax.persistence.EntityManager;

public class IndexedPartyAdapter implements PartyModel, JpaModel<PartyEntity> {

    private final EntityManager em;
    private final PartyEntity entity;

    public IndexedPartyAdapter(EntityManager em, PartyEntity entity) {
        this.em = em;
        this.entity = entity;
    }

    public static PartyEntity toEntity(PartyModel model, EntityManager em) {
        if (model instanceof IndexedPartyAdapter) {
            return ((IndexedPartyAdapter) model).getEntity();
        }
        return em.getReference(PartyEntity.class, model.getId());
    }

    @Override
    public PartyEntity getEntity() {
        return entity;
    }

    @Override
    public String getId() {
        return entity.getId();
    }

    @Override
    public String getAssignedId() {
        return entity.getAssignedId();
    }

    @Override
    public String getName() {
        return entity.getName();
    }


}
