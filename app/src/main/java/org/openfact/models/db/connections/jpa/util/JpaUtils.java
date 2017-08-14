package org.openfact.models.db.connections.jpa.util;

import javax.persistence.EntityManager;

public class JpaUtils {

    public static final String HIBERNATE_DEFAULT_SCHEMA = "hibernate.default_schema";

    public static String getTableNameForNativeQuery(String tableName, EntityManager em) {
        String schema = (String) em.getEntityManagerFactory().getProperties().get(HIBERNATE_DEFAULT_SCHEMA);
        return (schema==null) ? tableName : schema + "." + tableName;
    }

    /**
     * Get the name of custom table for liquibase updates for give ID of JpaEntityProvider
     * @param jpaEntityProviderFactoryId
     * @return table name
     */
    public static String getCustomChangelogTableName(String jpaEntityProviderFactoryId) {
        String upperCased = jpaEntityProviderFactoryId.toUpperCase();
        upperCased = upperCased.replaceAll("-", "_");
        upperCased = upperCased.replaceAll("[^A-Z_]", "");
        return "DATABASECHANGELOG_" + upperCased.substring(0, Math.min(10, upperCased.length()));
    }

}
