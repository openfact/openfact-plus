package org.openfact;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.jboss.logging.Logger;
import org.openfact.services.resources.KeycloakDeploymentConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class OAuth2Utils {

    private static final Logger logger = Logger.getLogger(OAuth2Utils.class);

    public static AuthorizationCodeFlow buildAuthCodeFlow(List<String> scopes) {
        KeycloakDeploymentConfig kcConfig = KeycloakDeploymentConfig.getInstance();

        return new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                new NetHttpTransport(),
                new JacksonFactory(),
                new GenericUrl(kcConfig.getTokenUrl()),
                new BasicAuthentication(kcConfig.getClientID(), kcConfig.getClientSecret()),
                kcConfig.getClientID(),
                kcConfig.getAuthorizationUrl())
                .setScopes(scopes)
                .build();
    }

    public static String buildRedirectURL(HttpServletRequest req) {
        String redirect = "?redirect=" + req.getParameter("redirect");
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/api/login/authorize_callback");

        String redirect_url = url.build() + redirect;
        logger.info("redirect_url:" + redirect_url);
        return redirect_url;
    }
}
