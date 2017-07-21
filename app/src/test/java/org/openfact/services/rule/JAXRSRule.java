package org.openfact.services.rule;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.keycloak.test.TestsHelper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.BitSet;

public class JAXRSRule implements TestRule {

    private final Client client;
    private final WebTarget target;

    private JAXRSRule(String uri) {
        this.client = ResteasyClientBuilder.newClient();
        this.target = client.target(uri);
    }

    public static JAXRSRule target(String uri) {
        return new JAXRSRule(uri);
    }

    public WebTarget target() {
        return this.target;
    }

    public Client client() {
        return this.client;
    }

    public Invocation.Builder build(String path) {
        return target.path(path).request(MediaType.APPLICATION_JSON);
    }

    public Invocation.Builder buildWithAuth(String username, String password, String path) {
        String token = TestsHelper.getToken(username, password, TestsHelper.testRealm);
        return target.path(path)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
            }
        };
    }


}
