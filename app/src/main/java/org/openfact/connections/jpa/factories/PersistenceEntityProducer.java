package org.openfact.connections.jpa.factories;


import org.openfact.connections.jpa.PersistenceExceptionConverter;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Proxy;

@Stateless
public class PersistenceEntityProducer {

    @PersistenceContext
    private EntityManager em;

    @Produces
    public EntityManager create() {
        return (EntityManager) Proxy.newProxyInstance(EntityManager.class.getClassLoader(), new Class[]{EntityManager.class}, new PersistenceExceptionConverter(em));
    }

}
