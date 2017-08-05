package org.openfact.services;

import org.jboss.logging.Logger;
import org.openfact.models.OpenfactSession;
import org.openfact.models.OpenfactSessionFactory;
import org.openfact.transaction.DefaultOpenfactTransactionManager;
import org.openfact.transaction.OpenfactTransactionManager;

public class DefaultOpenfactSession implements OpenfactSession {

    public static final Logger logger = Logger.getLogger(DefaultOpenfactSession.class);

    private final DefaultOpenfactSessionFactory factory;
    private final DefaultOpenfactTransactionManager transactionManager;

    public DefaultOpenfactSession(DefaultOpenfactSessionFactory factory) {
        this.factory = factory;
        this.transactionManager = new DefaultOpenfactTransactionManager(this);
    }

    @Override
    public OpenfactTransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public OpenfactSessionFactory getOpenfactSessionFactory() {
        return factory;
    }

    @Override
    public void close() {
        logger.debug("Closing Session...");
    }

}
