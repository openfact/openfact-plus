package org.clarksnut.oauth2;

import com.google.api.client.http.GenericUrl;
import org.clarksnut.services.resources.KeycloakDeploymentConfig;
import org.keycloak.adapters.KeycloakDeployment;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/logout")
public class OAuth2Logout extends HttpServlet {

    public static final String CALLBACK = "/api/logout_callback";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath(CALLBACK);
        String redirect_uri = url.build() + "?redirect=" + OAuth2Utils.getRedirect(req);

        KeycloakDeployment keycloakDeployment = KeycloakDeploymentConfig.getInstance().getDeployment();
        String logoutUrl = keycloakDeployment.getAuthServerBaseUrl()
                + "/realms/"
                + keycloakDeployment.getRealm()
                + "/protocol/openid-connect/logout"
                + "?redirect_uri=" + redirect_uri;
        resp.sendRedirect(logoutUrl);
    }

}
