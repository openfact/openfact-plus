package org.openfact.migration;

import org.jboss.logging.Logger;
import org.openfact.migration.migrators.Migration;
import org.openfact.models.MigrationModel;
import org.openfact.models.MigrationProvider;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Stateless
public class MigrationModelManager {

    private static Logger logger = Logger.getLogger(MigrationModelManager.class);

    @Inject
    @Any
    private Instance<Migration> migrations;

    @Inject
    private MigrationProvider migrationProvider;

    public void migrate() {
        Iterable<Migration> it = () -> migrations.iterator();
        List<Migration> listMigrations = StreamSupport.stream(it.spliterator(), false)
                .sorted(Comparator.comparingInt(Migration::priority))
                .collect(Collectors.toList());

        ModelVersion latest = listMigrations.get(listMigrations.size() - 1).getVersion();
        MigrationModel model = migrationProvider.getMigrationModel();
        ModelVersion stored = null;
        if (model.getStoredVersion() != null) {
            stored = new ModelVersion(model.getStoredVersion());
            if (latest.equals(stored)) {
                return;
            }
        }

        for (Migration m : listMigrations) {
            if (stored == null || stored.lessThan(m.getVersion())) {
                if (stored != null) {
                    logger.debugf("Migrating older model to %s", m.getVersion());
                }
                m.migrate();
            }
        }

        model.setStoredVersion(latest.toString());
    }

}
