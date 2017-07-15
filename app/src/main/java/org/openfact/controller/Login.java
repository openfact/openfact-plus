package org.openfact.controller;

import org.keycloak.AuthorizationContext;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.representations.idm.authorization.Permission;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/login/authorize")
public class Login {

    @Context
    private HttpServletRequest request;

    @GET
    public Response authorize(@QueryParam("redirect") String redirect) throws URISyntaxException {
        KeycloakSecurityContext keycloakSecurityContext = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        AuthorizationContext authzContext = keycloakSecurityContext.getAuthorizationContext();

        // Logout
        String logoutUrl = KeycloakUriBuilder.fromUri("/auth")
                .path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH)
                .queryParam("redirect_uri", redirect)
                .build("hello-world-authz").toString();
        System.out.println("Logout url: " + logoutUrl);

        // Permissions
        for (Permission permission : authzContext.getPermissions()) {
            System.out.println("Resource: " + permission.getResourceSetName());
            System.out.println("ID: " + permission.getResourceSetId());
        }

        return Response.temporaryRedirect(new URI(redirect)).build();
    }

}
