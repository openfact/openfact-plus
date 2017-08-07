package org.openfact.models.jpa;

import org.openfact.models.MigrationModel;
import org.openfact.models.MigrationProvider;

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
