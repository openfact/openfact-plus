package org.openfact.testsuite;

import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class TestUtil {

    public static JavaArchive[] getLibraries() {
        return Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve("org.mockito:mockito-core")
                .withTransitivity()
                .as(JavaArchive.class);
    }

    public static JavaArchive[] getGoogleLibraries() {
        return Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve(
                        "com.google.api-client:google-api-client",
                        "com.google.oauth-client:google-oauth-client-jetty",
                        "com.google.apis:google-api-services-gmail"
                )
                .withTransitivity()
                .as(JavaArchive.class);
    }

}
