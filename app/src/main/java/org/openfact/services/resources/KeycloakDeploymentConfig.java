package org.openfact.services.resources;

import org.keycloak.adapters.KeycloakDeployment;

public class KeycloakDeploymentConfig {

    private static KeycloakDeploymentConfig config = new KeycloakDeploymentConfig();

    private KeycloakDeployment keycloakDeployment;

    private KeycloakDeploymentConfig() {
    }

    public static KeycloakDeploymentConfig getInstance() {
        if (config == null) {
            config = new KeycloakDeploymentConfig();
        }
        return config;
    }

    public KeycloakDeployment getKeycloakDeployment() {
        return keycloakDeployment;
    }

    public void setKeycloakDeployment(KeycloakDeployment keycloakDeployment) {
        this.keycloakDeployment = keycloakDeployment;
    }
}
