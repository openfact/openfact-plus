package org.openfact.models.db.connections.jpa;

import javax.persistence.EntityManager;

public interface JpaConnectionProvider {

    EntityManager getEntityManager();

}

