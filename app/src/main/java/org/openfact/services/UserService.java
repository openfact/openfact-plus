package org.openfact.services;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.RefreshToken;
import org.keycloak.util.TokenUtil;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.models.utils.ModelToRepresentation;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.openfact.representation.idm.UserDataAttributes;
import org.openfact.representation.idm.UserRepresentation;

@Path("/user")
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class);

    @Inject
    private UserProvider userProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context final HttpServletRequest httpServletRequest) {
        KeycloakUtil kcUtil = new KeycloakUtil(httpServletRequest);
        String username = kcUtil.getUsername();

        // Get user
        UserModel user = this.userProvider.getByUsername(username);
        if (user == null) {
            user = this.userProvider.addUser(username);
        }

        // Check offline token
        if (user.getOfflineToken() == null) {
            RefreshableKeycloakSecurityContext ctx = (RefreshableKeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
            String refreshToken = ctx.getRefreshToken();
            try {
                RefreshToken refreshTokenDecoded = TokenUtil.getRefreshToken(refreshToken);
                boolean isOfflineToken = refreshTokenDecoded.getType().equals(TokenUtil.TOKEN_TYPE_OFFLINE);
                if (isOfflineToken) {
                    user.setOfflineToken(refreshToken);
                } else {
                    logger.warn("Was not possible to configure an offline token");
                }
            } catch (JWSInputException e) {
                logger.error("Could not read/refresh token");
                logger.warn("Was not possible to configure an offline token");
            }
        }

        return Response.ok(modelToRepresentation.toRepresentation(user)).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public void updateUser(@Context final HttpServletRequest request, final UserRepresentation representation) {
        KeycloakPrincipal<KeycloakSecurityContext> kcPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) request.getUserPrincipal();
        String username = kcPrincipal.getKeycloakSecurityContext().getIdToken().getPreferredUsername();

        UserModel user = userProvider.getByUsername(username);
        if (user == null) {
            throw new NotFoundException();
        }

        UserDataAttributes attributes = representation.getAttributes();
        if (attributes != null && attributes.isRegistrationCompleted() != null) {
            user.setRegistrationCompleted(attributes.isRegistrationCompleted());
        }
    }

}