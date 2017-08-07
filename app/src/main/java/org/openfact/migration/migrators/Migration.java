package org.openfact.migration.migrators;

import org.openfact.migration.ModelVersion;

public interface Migration {

    void migrate();

    ModelVersion getVersion();

    int priority();

}
