package org.openfact.services.resources.oauth2;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.jboss.logging.Logger;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.RefreshToken;
import org.keycloak.util.TokenUtil;
import org.openfact.models.exceptions.ModelException;
import org.openfact.representations.idm.TokenRepresentation;
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

    public static String buildRedirectURL(HttpServletRequest req, String callback) {
        String redirect = "?redirect=" + req.getParameter("redirect");
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath(callback);

        String redirect_url = url.build() + redirect;
        logger.debug("redirect_url:" + redirect_url);
        return redirect_url;
    }

    public static Credential buildCredential() {
        KeycloakDeploymentConfig kcConfig = KeycloakDeploymentConfig.getInstance();

        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(new NetHttpTransport())
                .setJsonFactory(new JacksonFactory())
                .setTokenServerUrl(new GenericUrl(kcConfig.getTokenUrl()))
                .setClientAuthentication(new BasicAuthentication(kcConfig.getClientID(), kcConfig.getClientSecret()))
                .build();
    }

    public static TokenRepresentation toRepresentation(Credential credential) {
        RefreshToken refreshToken;
        try {
            refreshToken = TokenUtil.getRefreshToken(credential.getRefreshToken());
        } catch (JWSInputException e) {
            throw new ModelException("Could not parse refresh token", e);
        }

        TokenRepresentation token = new TokenRepresentation();
        token.setToken_type("Bearer");
        token.setAccess_token(credential.getAccessToken());
        token.setExpires_in(credential.getExpirationTimeMilliseconds());
        token.setRefresh_token(credential.getRefreshToken());
        token.setRefresh_expires_in((long) refreshToken.getExpiration());

        return token;
    }

}
