package org.clarksnut.services.util;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.util.TokenUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class SSOContext {

    private final HttpServletRequest httpServletRequest;

    public SSOContext(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public AccessToken getParsedAccessToken() {
        KeycloakPrincipal<KeycloakSecurityContext> kcPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        return kcPrincipal.getKeycloakSecurityContext().getToken();
    }

    public String getUsername() {
        KeycloakPrincipal<KeycloakSecurityContext> kcPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        return kcPrincipal.getKeycloakSecurityContext().getToken().getPreferredUsername();
    }

    public Map<String, Object> getOtherClaims() {
        KeycloakPrincipal<KeycloakSecurityContext> kcPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        return kcPrincipal.getKeycloakSecurityContext().getToken().getOtherClaims();
    }

    public String getToken() throws JWSInputException {
        RefreshableKeycloakSecurityContext ctx = (RefreshableKeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
        return ctx.getTokenString();
    }

    public RefreshToken getRefreshedToken() throws JWSInputException {
        RefreshableKeycloakSecurityContext ctx = (RefreshableKeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
        String refreshToken = ctx.getRefreshToken();
        return TokenUtil.getRefreshToken(refreshToken);
    }

    public boolean isOfflineToken(String refreshToken) throws JWSInputException {
        RefreshToken refreshTokenDecoded = TokenUtil.getRefreshToken(refreshToken);
        return refreshTokenDecoded.getType().equals(TokenUtil.TOKEN_TYPE_OFFLINE);
    }

    public String getRealm() {
        KeycloakPrincipal<KeycloakSecurityContext> kcPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        return kcPrincipal.getKeycloakSecurityContext().getRealm();
    }

    public String getAccessToken() {
        KeycloakPrincipal<KeycloakSecurityContext> kcPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        return kcPrincipal.getKeycloakSecurityContext().getTokenString();
    }

    public String getAuthServerBaseUrl() {
        AdapterDeploymentContext deploymentContext = (AdapterDeploymentContext) httpServletRequest.getServletContext().getAttribute(AdapterDeploymentContext.class.getName());
        KeycloakDeployment deployment = deploymentContext.resolveDeployment(null);
        return deployment.getAuthServerBaseUrl();
    }

}
