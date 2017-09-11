package org.openfact.services.resources;

import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.adapters.KeycloakDeployment;

public class KeycloakDeploymentConfig {

    private static KeycloakDeploymentConfig config = new KeycloakDeploymentConfig();

    private KeycloakDeployment deployment;
    private JsonNode endpoints;

    private KeycloakDeploymentConfig() {
    }

    public static KeycloakDeploymentConfig getInstance() {
        if (config == null) {
            config = new KeycloakDeploymentConfig();
        }
        return config;
    }

    public KeycloakDeployment getDeployment() {
        return deployment;
    }

    public void setDeployment(KeycloakDeployment deployment) {
        this.deployment = deployment;
    }

    public JsonNode getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(JsonNode endpoints) {
        this.endpoints = endpoints;
    }

    public String getTokenEndpoint() {
        return endpoints.get("token_endpoint").textValue();
    }

    public String getAuthorizationEndpoint() {
        return endpoints.get("authorization_endpoint").textValue();
    }
}
