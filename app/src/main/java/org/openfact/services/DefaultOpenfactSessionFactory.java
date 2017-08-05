package org.openfact.services;

import org.jboss.logging.Logger;
import org.openfact.models.OpenfactSession;
import org.openfact.models.OpenfactSessionFactory;

public class DefaultOpenfactSessionFactory implements OpenfactSessionFactory {

    public static final Logger logger = Logger.getLogger(DefaultOpenfactSessionFactory.class);

    @Override
    public OpenfactSession create() {
        return new DefaultOpenfactSession(this);
    }

    @Override
    public void close() {
        logger.debug("Closing Session Factory...");
    }

}
