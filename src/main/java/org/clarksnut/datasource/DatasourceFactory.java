package org.clarksnut.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class DatasourceFactory {

    private static DatasourceFactory INSTANCE;

    private Map<String, DatasourceProvider> providers = new HashMap<>();

    private DatasourceFactory() {
        synchronized (this) {
            ServiceLoader<DatasourceProvider> serviceLoader = ServiceLoader.load(DatasourceProvider.class);
            for (DatasourceProvider e : serviceLoader) {
                providers.put(e.getName(), e);
            }
        }
    }

    public static DatasourceFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatasourceFactory();
        }
        return INSTANCE;
    }

    public DatasourceProvider getDatasourceProvider(String datasource) {
        return providers.get(datasource);
    }

}
