package org.clarksnut.datasource;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.annotation.Annotation;

@Stateless
public class DatasourceUtil {

    @Inject
    @Any
    private Instance<DatasourceProvider> datasourceProviders;

    public DatasourceProvider getDatasourceProvider(String datasource) {
        Annotation annotation = new DatasourceTypeLiteral(datasource);
        Instance<DatasourceProvider> instance = datasourceProviders.select(annotation);
        if (instance.isAmbiguous() || instance.isUnsatisfied()) {
            return null;
        }
        return instance.get();
    }
}
