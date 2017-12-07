package org.clarksnut.services.resources;

import org.jboss.logging.Logger;
import org.keycloak.adapters.KeycloakDeployment;

public class KeycloakDeploymentConfig {

    private static final Logger logger = Logger.getLogger(KeycloakDeploymentConfig.class);

    private static KeycloakDeploymentConfig config = new KeycloakDeploymentConfig();

    private KeycloakDeployment deployment;

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

    public String getClientID() {
        return deployment.getResourceName();
    }

    public String getClientSecret() {
        return (String) deployment.getResourceCredentials().get("secret");
    }

    public String getTokenUrl() {
        return deployment.getAuthServerBaseUrl() + "/realms/" + deployment.getRealm() + "/protocol/openid-connect/token";
    }

    public String getAuthorizationUrl() {
        return deployment.getAuthServerBaseUrl() + "/realms/" + deployment.getRealm() + "/protocol/openid-connect/auth";
    }
}
