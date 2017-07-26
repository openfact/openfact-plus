package org.openfact.services.services;

import org.jboss.logging.Logger;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.util.TokenUtil;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.models.utils.ModelToRepresentation;
import org.openfact.representation.idm.ContextInformationRepresentation;
import org.openfact.representation.idm.ExtProfileRepresentation;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;

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
    public Response getCurrentUser(@Context final HttpServletRequest httpServletRequest) throws ExecutionException, InterruptedException {
        KeycloakUtil kcUtil = new KeycloakUtil(httpServletRequest);
        String username = kcUtil.getUsername();

        // Get user
        UserModel user = this.userProvider.getByUsername(username);
        if (user == null) {
            user = this.userProvider.addUser(username);
        }

        return Response.ok(modelToRepresentation.toRepresentation(user)).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@Context final HttpServletRequest httpServletRequest, final ExtProfileRepresentation extProfile) {
        KeycloakUtil kcUtil = new KeycloakUtil(httpServletRequest);
        String username = kcUtil.getUsername();

        UserModel user = userProvider.getByUsername(username);
        if (user == null) {
            throw new NotFoundException();
        }

        // Offline token
        ContextInformationRepresentation contextInformation = extProfile.getContextInformation();
        if (contextInformation != null) {
            String offlineToken = contextInformation.getOfflineToken();
            if (offlineToken != null) {
                try {
                    if (TokenUtil.isOfflineToken(offlineToken)) {
                        user.setOfflineToken(offlineToken);
                    } else {
                        return ErrorResponse.error("Invalid Token Type", Response.Status.BAD_REQUEST);
                    }
                } catch (JWSInputException e) {
                    logger.error("Could not read sended token", e);
                }
            }
        }

        // Is registration completed
        Boolean registrationCompleted = extProfile.getRegistrationCompleted();
        if (registrationCompleted != null) {
            user.setRegistrationCompleted(registrationCompleted);
        }


        return Response.ok(modelToRepresentation.toRepresentation(user)).build();
    }

}