package org.openfact.models.jpa;

import org.openfact.models.jpa.entities.SynchronizationEntity;
import org.openfact.syncronization.SyncronizationModel;

import javax.persistence.EntityManager;
import java.math.BigInteger;

public class SyncronizationAdapter implements SyncronizationModel {

    private EntityManager em;

    public SyncronizationAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    public BigInteger getHistoryId() {
        SynchronizationEntity entity = em.find(SynchronizationEntity.class, SynchronizationEntity.SINGLETON_ID);
        if (entity == null) return null;
        return entity.getStartHistoryId();
    }

    @Override
    public void setHistoryId(BigInteger historyId) {
        SynchronizationEntity entity = em.find(SynchronizationEntity.class, SynchronizationEntity.SINGLETON_ID);
        if (entity == null) {
            entity = new SynchronizationEntity();
            entity.setId(SynchronizationEntity.SINGLETON_ID);
            entity.setStartHistoryId(historyId);
            em.persist(entity);
        } else {
            entity.setStartHistoryId(historyId);
            em.flush();
        }
    }

}
