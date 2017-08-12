package org.openfact.models.jpa.connections;

import javax.persistence.EntityManager;

public interface JpaConnectionProvider {

    EntityManager getEntityManager();

}

