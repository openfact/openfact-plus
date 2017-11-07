package org.openfact.report;

import javax.inject.Qualifier;
import java.lang.annotation.*;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Documented
public @interface ReportProviderType {

    Type type();

    enum Type {
        EXTENDING, FOLDER, JAR
    }

}
