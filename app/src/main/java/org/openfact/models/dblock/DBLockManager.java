package org.openfact.models.dblock;

import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class DBLockManager {

    protected static final Logger logger = Logger.getLogger(DBLockManager.class);

    @Inject
    private DBLockProvider lock;

    public void checkForcedUnlock() {
        if (Boolean.getBoolean("openfact.dblock.forceUnlock")) {
            if (lock.supportsForcedUnlock()) {
                logger.warn("Forced release of DB lock at startup requested by System property. Make sure to not use this in production environment! And especially when more cluster nodes are started concurrently.");
                lock.releaseLock();
            } else {
                throw new IllegalStateException("Forced unlock requested, but provider " + lock + " doesn't support it");
            }
        }
    }

}
