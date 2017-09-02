package org.openfact.services.resources;

import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.io.InputStream;

@ApplicationPath("/api")
public class OpenfactApplication extends Application {

    public OpenfactApplication(@Context ServletContext context) {
        InputStream config = context.getResourceAsStream("/WEB-INF/keycloak.json");
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(config);
        KeycloakDeploymentConfig.getInstance().setKeycloakDeployment(deployment);
    }

}
