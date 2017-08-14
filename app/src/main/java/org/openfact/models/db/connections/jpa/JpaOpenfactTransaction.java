package org.openfact.models.db.connections.jpa;

import org.openfact.transaction.OpenfactTransaction;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

public class JpaOpenfactTransaction implements OpenfactTransaction {

    protected EntityManager em;

    public JpaOpenfactTransaction(EntityManager em) {
        this.em = em;
    }

    @Override
    public void begin() {
        em.getTransaction().begin();
    }

    @Override
    public void commit() {
        try {
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            throw PersistenceExceptionConverter.convert(e.getCause() != null ? e.getCause() : e);
        }
    }

    @Override
    public void rollback() {
        em.getTransaction().rollback();
    }

    @Override
    public void setRollbackOnly() {
        em.getTransaction().setRollbackOnly();
    }

    @Override
    public boolean getRollbackOnly() {
        return em.getTransaction().getRollbackOnly();
    }

    @Override
    public boolean isActive() {
        return em.getTransaction().isActive();
    }

}
