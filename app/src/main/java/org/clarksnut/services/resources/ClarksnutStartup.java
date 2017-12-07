package org.clarksnut.services.resources;

import org.jboss.logging.Logger;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.InputStream;

@WebServlet(loadOnStartup = 1)
public class ClarksnutStartup extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ClarksnutStartup.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();

        logger.info("Getting keycloak.json");
        InputStream is = context.getResourceAsStream("/WEB-INF/keycloak.json");

        if (is != null) {
            logger.info("Parsing keycloak.json");
            KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(is);
            logger.info("keycloak.json parsed");

            KeycloakDeploymentConfig instance = KeycloakDeploymentConfig.getInstance();
            instance.setDeployment(deployment);
            logger.info("keycloak.json saved on " + KeycloakDeploymentConfig.class.getName());
        } else {
            logger.error("Could not find keycloak.json on classpath");
        }
    }

}
