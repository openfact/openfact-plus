package org.clarksnut.batchs.support.utils;

import org.jboss.logging.Logger.Level;

import javax.batch.api.BatchProperty;
import javax.inject.Inject;

public abstract class AbstractLoggerListener {

    /**
     * A name assigned to the stored procedure query in JPA metadata.
     * Optional property, and defaults to info.
     */
    @Inject
    @BatchProperty
    protected String loggerLevel;

    private Level level;

    protected String getDefaultLevel() {
        return Level.INFO.name();
    }

    protected Level getLevel() {
        if (level == null) {
            level = Level.valueOf(loggerLevel != null ? loggerLevel.toUpperCase() : getDefaultLevel());
        }
        return level;
    }

}
