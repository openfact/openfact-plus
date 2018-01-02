package org.clarksnut.theme;

import javax.inject.Qualifier;
import java.lang.annotation.*;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Documented
public @interface ThemeProviderType {

    Type type();

    enum Type {
        EXTENDING, FOLDER, JAR
    }

}