package org.openfact.migration.migrators;

import org.openfact.migration.ModelVersion;

public class MigrateTo1_0_0 implements Migration {

    public static final ModelVersion VERSION = new ModelVersion("1.0.0");

    @Override
    public void migrate() {

    }

    @Override
    public ModelVersion getVersion() {
        return VERSION;
    }

    @Override
    public int priority() {
        return 1;
    }

}
