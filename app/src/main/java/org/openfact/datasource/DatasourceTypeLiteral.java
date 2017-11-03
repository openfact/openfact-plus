package org.openfact.datasource;

import javax.enterprise.util.AnnotationLiteral;

public class DatasourceTypeLiteral extends AnnotationLiteral<DatasourceType> implements DatasourceType {

    private final String datasource;

    public DatasourceTypeLiteral(String datasource) {
        this.datasource = datasource;
    }

    @Override
    public String datasource() {
        return datasource;
    }

}
