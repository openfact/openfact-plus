package org.clarksnut.oauth2;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.clarksnut.models.exceptions.ModelException;
import org.clarksnut.representations.idm.TokenRepresentation;
import org.clarksnut.services.resources.KeycloakDeploymentConfig;
import org.jboss.logging.Logger;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.RefreshToken;
import org.keycloak.util.TokenUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class OAuth2Utils {

    private static final Logger logger = Logger.getLogger(OAuth2Utils.class);

    public static final String REDIRECT_REQUEST_ATTRIBUTE_NAME = "redirect";

    public static AuthorizationCodeFlow getAuthorizationCodeFlow(List<String> scopes) {
        return getAuthorizationCodeFlowBuilder()
                .setScopes(scopes)
                .build();
    }

    public static AuthorizationCodeFlow.Builder getAuthorizationCodeFlowBuilder() {
        KeycloakDeploymentConfig kcDeploymentConfig = KeycloakDeploymentConfig.getInstance();

        return new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                new NetHttpTransport(),
                new JacksonFactory(),
                new GenericUrl(kcDeploymentConfig.getTokenUrl()),
                new BasicAuthentication(kcDeploymentConfig.getClientID(), kcDeploymentConfig.getClientSecret()),
                kcDeploymentConfig.getClientID(),
                kcDeploymentConfig.getAuthorizationUrl());
    }

    public static String buildRedirectURL(HttpServletRequest req, String callback, String redirect) {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath(callback);

        String redirect_url = url.build() + "?redirect=" + redirect;
        logger.debug("redirect_url:" + redirect_url);
        return redirect_url;
    }

    public static String getRedirect(HttpServletRequest req) throws ServletException {
        String redirect = req.getParameter(REDIRECT_REQUEST_ATTRIBUTE_NAME);
        if (redirect == null) {
            throw new ServletException("Request attribute[" + REDIRECT_REQUEST_ATTRIBUTE_NAME + "] required");
        }
        return redirect;
    }

    public static Credential getCredential() {
        KeycloakDeploymentConfig kcDeploymentConfig = KeycloakDeploymentConfig.getInstance();

        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(new NetHttpTransport())
                .setJsonFactory(new JacksonFactory())
                .setTokenServerUrl(new GenericUrl(kcDeploymentConfig.getTokenUrl()))
                .setClientAuthentication(new BasicAuthentication(kcDeploymentConfig.getClientID(), kcDeploymentConfig.getClientSecret()))
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
