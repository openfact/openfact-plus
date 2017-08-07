package org.openfact.connections.jpa;

import javax.persistence.EntityManager;

public interface JpaConnectionProvider {

    EntityManager getEntityManager();

}

