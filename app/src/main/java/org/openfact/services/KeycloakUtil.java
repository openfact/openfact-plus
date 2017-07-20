package org.openfact.services;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class KeycloakUtil {

    private final HttpServletRequest httpServletRequest;

    public KeycloakUtil(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public String getUsername() {
        KeycloakPrincipal<KeycloakSecurityContext> kcPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        return kcPrincipal.getKeycloakSecurityContext().getIdToken().getPreferredUsername();
    }

    public Map<String, Object> getOtherClaims() {
        KeycloakPrincipal<KeycloakSecurityContext> kcPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        return kcPrincipal.getKeycloakSecurityContext().getToken().getOtherClaims();
    }

}
