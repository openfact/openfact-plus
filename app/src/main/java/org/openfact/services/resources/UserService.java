package org.openfact.services.resources;

import org.jboss.logging.Logger;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.util.TokenUtil;
import org.openfact.models.Constants;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.models.utils.ModelToRepresentation;
import org.openfact.representation.idm.ContextInformationRepresentation;
import org.openfact.representation.idm.ExtProfileRepresentation;
import org.openfact.services.ErrorResponse;
import org.openfact.services.managers.SpaceManager;
import org.openfact.services.util.SSOContext;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Stateless
@Path("/user")
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class);

    @Inject
    private UserProvider userProvider;

    @Inject
    private SpaceManager spaceManager;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context final HttpServletRequest httpServletRequest) {
        SSOContext ssoContext = new SSOContext(httpServletRequest);
        String username = ssoContext.getUsername();

        // Get user
        UserModel user = this.userProvider.getByUsername(username);
        if (user == null) {
            user = this.userProvider.addUser(username);

            Map<String, Object> userAttributes = ssoContext.getOtherClaims();
            if (userAttributes.containsKey(Constants.KC_SPACE_ATTRIBUTE_NAME)) {
                String claimedAccountId = null;

                // Even if there is multiple spaces, at the first time we should use just the fist one
                Object o = userAttributes.get(Constants.KC_SPACE_ATTRIBUTE_NAME);
                if (o instanceof Collection) {
                    Iterator it = ((Collection) o).iterator();
                    while (it.hasNext()) {
                        claimedAccountId = (String) it.next();
                        break;
                    }
                } else {
                    claimedAccountId = (String) o;
                }

                // Claim or request account id
                if (claimedAccountId != null) {
                    spaceManager.claimAccountId(claimedAccountId, user);
                }
            }
        }

        return Response.ok(modelToRepresentation.toRepresentation(user)).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@Context final HttpServletRequest httpServletRequest, final ExtProfileRepresentation extProfile) {
        String username = new SSOContext(httpServletRequest).getUsername();

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
                        user.setOfflineRefreshToken(offlineToken);
                    } else {
                        return ErrorResponse.error("Invalid Token Type", Response.Status.BAD_REQUEST);
                    }
                } catch (JWSInputException e) {
                    logger.error("Could not decode token", e);
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