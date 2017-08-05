package org.openfact.transaction.jboss;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.openfact.transaction.JtaTransactionManagerLookup;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;

public class JBossJtaTransactionManagerLookup implements JtaTransactionManagerLookup {

    private static final Logger logger = Logger.getLogger(JBossJtaTransactionManagerLookup.class);
    private TransactionManager tm;

    @Override
    public TransactionManager getTransactionManager() {
        return tm;
    }

    @Override
    public void init(Config.Scope config) {
        try {
            InitialContext ctx = new InitialContext();
            tm = (TransactionManager)ctx.lookup("java:jboss/TransactionManager");
            if (tm == null) {
                logger.debug("Could not locate TransactionManager");
            }
        } catch (NamingException e) {
            logger.debug("Could not load TransactionManager", e);
        }

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public String getId() {
        return "jboss";
    }

}
