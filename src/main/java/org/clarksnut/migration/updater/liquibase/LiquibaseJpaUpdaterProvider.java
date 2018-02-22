package org.clarksnut.migration.updater.liquibase;

public class LiquibaseJpaUpdaterProvider {

    public static String getTable(String table, String defaultSchema) {
        return defaultSchema != null ? defaultSchema + "." + table : table;
    }

}
