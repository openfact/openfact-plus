package org.clarksnut.services.resources;

import org.jboss.logging.Logger;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Optional;

@WebServlet(loadOnStartup = 1)
public class ClarksnutStartup extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ClarksnutStartup.class);

    @Inject
    @ConfigurationValue("swarm.keycloak.json.path")
    private Optional<String> keycloakJsonPath;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();

        InputStream is;
        if (keycloakJsonPath.isPresent()) {
            String keycloakPath = keycloakJsonPath.get();
            logger.info("Reading keycloak.json from" + keycloakPath);
            try {
                is = new FileInputStream(Paths.get(keycloakPath).toFile());
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Could not read keycloak.json from " + keycloakPath);
            }
        } else {
            logger.info("Reading keycloak.json from classpath");
            is = context.getResourceAsStream("/WEB-INF/keycloak.json");
        }

        logger.info("Parsing keycloak.json");
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(is);
        logger.info("keycloak.json parsed");

        KeycloakDeploymentConfig instance = KeycloakDeploymentConfig.getInstance();
        instance.setDeployment(deployment);
        logger.info("keycloak.json saved on " + KeycloakDeploymentConfig.class.getName());
    }

}
