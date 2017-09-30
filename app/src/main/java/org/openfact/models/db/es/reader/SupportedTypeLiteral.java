package org.openfact.models.db.es.reader;

import javax.enterprise.util.AnnotationLiteral;

public class SupportedTypeLiteral extends AnnotationLiteral<SupportedType> implements SupportedType {

    private final String value;

    public SupportedTypeLiteral(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

}
