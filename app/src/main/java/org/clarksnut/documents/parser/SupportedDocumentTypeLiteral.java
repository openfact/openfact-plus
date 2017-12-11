package org.clarksnut.documents.parser;

import javax.enterprise.util.AnnotationLiteral;

public class SupportedDocumentTypeLiteral extends AnnotationLiteral<SupportedDocumentType> implements SupportedDocumentType {

    private final String value;

    public SupportedDocumentTypeLiteral(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

}
