package org.openfact.services.resources;

import org.jboss.logging.Logger;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.io.InputStream;

@ApplicationPath("/api")
public class OpenfactApplication extends Application {

    private static final Logger logger = Logger.getLogger(OpenfactApplication.class);

    public OpenfactApplication(@Context ServletContext context) {
        logger.info("Getting keycloak.json");
        InputStream config = context.getResourceAsStream("/WEB-INF/keycloak.json");

        logger.info("Parsing keycloak.json");
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(config);
        logger.info("keycloak.json parsed");

        KeycloakDeploymentConfig instance = KeycloakDeploymentConfig.getInstance();
        instance.setKeycloakDeployment(deployment);
        logger.info("keycloak.json saved on " + KeycloakDeploymentConfig.class.getName());
    }

}
