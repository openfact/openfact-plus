package org.openfact.models.jpa;

import org.openfact.models.jpa.entities.SyncronizationEntity;
import org.openfact.syncronization.SyncronizationModel;

import javax.persistence.EntityManager;
import java.math.BigInteger;

public class SyncronizationAdapter implements SyncronizationModel{

    private EntityManager em;

    public SyncronizationAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    public BigInteger getHistoryId() {
        SyncronizationEntity entity = em.find(SyncronizationEntity.class, SyncronizationEntity.SINGLETON_ID);
        if (entity == null) return null;
        return entity.getHistoryId();
    }

    @Override
    public void setHistoryId(BigInteger historyId) {
        SyncronizationEntity entity = em.find(SyncronizationEntity.class, SyncronizationEntity.SINGLETON_ID);
        if (entity == null) {
            entity = new SyncronizationEntity();
            entity.setId(SyncronizationEntity.SINGLETON_ID);
            entity.setHistoryId(historyId);
            em.persist(entity);
        } else {
            entity.setHistoryId(historyId);
            em.flush();
        }
    }

}
