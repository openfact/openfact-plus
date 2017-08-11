package org.openfact.services.util;

import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;

import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import java.io.InputStream;

@Singleton
public class SSODeploymentProducer {

    private KeycloakDeployment deployment;

    @Produces
    public KeycloakDeployment produces() {
        if (deployment == null) {
            InputStream is = getClass().getResourceAsStream("keycloak.json");
            if (is == null) {
                throw new IllegalStateException("Not able to find the file keycloak.json");
            }
            deployment = KeycloakDeploymentBuilder.build(is);
        }
        return deployment;
    }

}
