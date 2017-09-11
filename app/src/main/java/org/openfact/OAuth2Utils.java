package org.openfact;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.openfact.services.resources.KeycloakDeploymentConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class OAuth2Utils {

    public static AuthorizationCodeFlow buildAuthCodeFlow(List<String> scopes) {
        KeycloakDeploymentConfig kcConfig = KeycloakDeploymentConfig.getInstance();

        return new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                new NetHttpTransport(),
                new JacksonFactory(),
                new GenericUrl("http://keycloak-openfact-dev.192.168.42.56.nip.io/auth/realms/openfact/protocol/openid-connect/token"),
                new BasicAuthentication("openfact", "51d552f9-16e3-4544-9754-aa02f2cff3de"),
                "openfact",
                "http://keycloak-openfact-dev.192.168.42.56.nip.io/auth/realms/openfact/protocol/openid-connect/auth")
                .setScopes(scopes)
                .build();
    }

    public static String buildRedirectURL(HttpServletRequest req) {
        String redirect = "?redirect=" + req.getParameter("redirect");
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/api/login/authorize_callback");
        return url.build() + redirect;
    }
}
