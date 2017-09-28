package org.openfact.models.db.es.reader;

import javax.enterprise.util.AnnotationLiteral;

public class LocationTypeLiteral extends AnnotationLiteral<LocationType> implements LocationType {

    private final String value;

    public LocationTypeLiteral(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

}
