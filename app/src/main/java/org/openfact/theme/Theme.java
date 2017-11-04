package org.openfact.theme;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

public interface Theme {

    enum Type {EMAIL, WELCOME, COMMON}

    String getName();

    String getParentName();

    String getImportName();

    Type getType();

    URL getTemplate(String name) throws IOException;

    InputStream getTemplateAsStream(String name) throws IOException;

    URL getResource(String path) throws IOException;

    InputStream getResourceAsStream(String path) throws IOException;

    /**
     * Same as getMessages(baseBundlename, locale), but uses a default baseBundlename
     * such as "messages".
     *
     * @param locale The locale of the desired message bundle.
     * @return The localized messages from the bundle.
     * @throws IOException If bundle can not be read.
     */
    Properties getMessages(Locale locale) throws IOException;

    /**
     * Retrieve localized messages from a message bundle.
     *
     * @param baseBundlename The base name of the bundle, such as "messages" in
     *                       messages_en.properties.
     * @param locale         The locale of the desired message bundle.
     * @return The localized messages from the bundle.
     * @throws IOException If bundle can not be read.
     */
    Properties getMessages(String baseBundlename, Locale locale) throws IOException;

    Properties getProperties() throws IOException;

}
