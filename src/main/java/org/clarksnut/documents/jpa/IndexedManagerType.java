package org.clarksnut.documents.jpa;

import javax.inject.Qualifier;
import java.lang.annotation.*;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Documented
public @interface IndexedManagerType {

    Type type();

    enum Type {
        ELASTICSEARCH, LUCENE
    }

}
