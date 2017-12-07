package org.clarksnut.models.db.jpa;

import org.clarksnut.models.MigrationModel;
import org.clarksnut.models.MigrationProvider;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Stateless
public class JpaMigrationProvider implements MigrationProvider {

    @Inject
    private EntityManager em;

    @Override
    public MigrationModel getMigrationModel() {
        return new MigrationModelAdapter(em);
    }

}
