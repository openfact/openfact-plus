package org.clarksnut.models.db.connections;

import javax.persistence.EntityManager;

public interface JpaConnectionProvider {

    EntityManager getEntityManager();

}

