package org.openfact.datasource;

import javax.enterprise.util.AnnotationLiteral;

public class DatasourceTypeLiteral extends AnnotationLiteral<DatasourceType> implements DatasourceType {

    private final String documentType;
    private final String region;

    public DatasourceTypeLiteral(String documentType, String region) {
        this.documentType = documentType;
        this.region = region;
    }

    @Override
    public String region() {
        return region;
    }

    @Override
    public String documentType() {
        return documentType;
    }
}
