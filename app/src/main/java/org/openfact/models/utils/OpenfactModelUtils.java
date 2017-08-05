package org.openfact.models.utils;

import org.openfact.models.OpenfactSession;
import org.openfact.models.OpenfactSessionFactory;
import org.openfact.models.OpenfactSessionTask;
import org.openfact.transaction.OpenfactTransaction;

import java.util.UUID;

public class OpenfactModelUtils {

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Wrap given runnable job into OpenfactTransaction.
     *
     * @param task
     */
    public static void runJobInTransaction(OpenfactSessionFactory factory, OpenfactSessionTask task) {
        OpenfactSession session = factory.create();
        OpenfactTransaction tx = session.getTransactionManager();
        try {
            tx.begin();
            task.run();

            if (tx.isActive()) {
                if (tx.getRollbackOnly()) {
                    tx.rollback();
                } else {
                    tx.commit();
                }
            }
        } catch (RuntimeException re) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw re;
        } finally {
            session.close();
        }
    }

}
