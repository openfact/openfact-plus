package org.openfact.models.jpa;

import org.openfact.models.MigrationModel;
import org.openfact.models.jpa.entity.MigrationModelEntity;

import javax.persistence.EntityManager;

public class MigrationModelAdapter implements MigrationModel {

    private EntityManager em;

    public MigrationModelAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    public String getStoredVersion() {
        MigrationModelEntity entity = em.find(MigrationModelEntity.class, MigrationModelEntity.SINGLETON_ID);
        if (entity == null) return null;
        return entity.getVersion();
    }

    @Override
    public void setStoredVersion(String version) {
        MigrationModelEntity entity = em.find(MigrationModelEntity.class, MigrationModelEntity.SINGLETON_ID);
        if (entity == null) {
            entity = new MigrationModelEntity();
            entity.setId(MigrationModelEntity.SINGLETON_ID);
            entity.setVersion(version);
            em.persist(entity);
        } else {
            entity.setVersion(version);
            em.flush();
        }
    }
}
