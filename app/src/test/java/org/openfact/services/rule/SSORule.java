package org.openfact.services.rule;

import org.junit.rules.ExternalResource;
import org.keycloak.test.TestsHelper;

import java.io.IOException;

import static org.keycloak.test.TestsHelper.*;

public class SSORule extends ExternalResource {

    @Override
    protected void before() {
        try {
            TestsHelper.appName = "arquillian-test";
            TestsHelper.keycloakBaseUrl = "http://localhost:8180/auth"; // SSO url
//            try {
//                deleteRealm("admin", "admin", "test-realm");
//            } catch (Throwable e) {
//            }
            importTestRealm("admin", "admin", "/test-realm.json");
            createDirectGrantClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void after() {
        try {
            cleanUp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void cleanUp() throws IOException {
        deleteRealm("admin", "admin", TestsHelper.testRealm);
    }

}
