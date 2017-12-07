package org.clarksnut.models.db;

import org.hibernate.Session;

import javax.persistence.EntityManager;

public abstract class HibernateProvider {

    protected abstract EntityManager getEntityManager();

    protected Session getSession() {
        return getEntityManager().unwrap(Session.class);
    }

}
