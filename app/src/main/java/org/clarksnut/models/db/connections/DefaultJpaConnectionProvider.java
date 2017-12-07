package org.clarksnut.models.db.connections;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class DefaultJpaConnectionProvider implements JpaConnectionProvider {

    @PersistenceContext
    private EntityManager em;

    @Produces
    public EntityManager getEntityManager() {
        return PersistenceExceptionConverter.create(em);
    }

}
