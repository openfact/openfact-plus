package org.openfact.services.services;

import org.jboss.logging.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.jose.jws.JWSInputException;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.models.utils.ModelToRepresentation;
import org.openfact.representation.idm.UserDataAttributesRepresentation;
import org.openfact.representation.idm.UserRepresentation;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Path("/user")
@Stateless
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class);

    @Inject
    private UserProvider userProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @Resource
    private ManagedExecutorService mes;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context HttpServletRequest httpServletRequest) throws ExecutionException, InterruptedException {
        KeycloakUtil kcUtil = new KeycloakUtil(httpServletRequest);
        String username = kcUtil.getUsername();

        // Get user
        UserModel user = this.userProvider.getByUsername(username);
        if (user == null) {
            user = this.userProvider.addUser(username);
        }

        // Check offline token
        if (user.getOfflineToken() == null) {
            try {
                String refreshToken = kcUtil.getToken();
                if (kcUtil.isOfflineToken(refreshToken)) {
                    user.setOfflineToken(refreshToken);
                } else {
                    logger.warn("The token used is not an offline token");
                }
            } catch (JWSInputException e) {
                logger.error("Could not read/refresh token", e);
            }
        }

        if (user.getOfflineToken() != null) {
            /*Map<String, Future<Boolean>> futures = new HashMap<>();
            for (SupportedIDP idp : SupportedIDP.values()) {
                futures.put(idp.getBrokerName(), mes.submit(new IDPBrokerChecker(kcUtil.getAuthServerBaseUrl(), kcUtil.getRealm(), kcUtil.getAccessToken(), idp.getBrokerName())));
            }

            Set<String> identities = new HashSet<>();
            for (Map.Entry<String, Future<Boolean>> entry : futures.entrySet()) {
                Boolean token = entry.getValue().get();
                if (token != null && token.equals(true)) {
                    identities.add(entry.getKey());
                }
            }
            user.setIdentities(identities);*/
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

        UserDataAttributesRepresentation attributes = representation.getAttributes();
        if (attributes != null && attributes.isRegistrationCompleted() != null) {
            user.setRegistrationCompleted(attributes.isRegistrationCompleted());
        }
    }

}