package org.openfact.models.jpa;

import org.jboss.logging.Logger;
import org.openfact.models.OpenfactProvider;
import org.openfact.syncronization.SyncronizationModel;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class JpaOpenfactProvider implements OpenfactProvider {

    protected static final Logger logger = Logger.getLogger(JpaOpenfactProvider.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public SyncronizationModel getSyncronizationModel() {
        return new SyncronizationAdapter(em);
    }

}
