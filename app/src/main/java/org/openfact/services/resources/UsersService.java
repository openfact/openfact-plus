package org.openfact.services.resources;

import org.jboss.logging.Logger;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.util.TokenUtil;
import org.openfact.models.*;
import org.openfact.models.utils.ModelToRepresentation;
import org.openfact.representation.idm.*;
import org.openfact.services.ErrorResponse;
import org.openfact.services.managers.UserManager;
import org.openfact.services.util.SSOContext;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
@Path("/users")
public class UsersService {

    private static final Logger logger = Logger.getLogger(UsersService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private SpaceProvider spaceProvider;

    @Inject
    private UserProvider userProvider;

    @Inject
    private UserManager userManager;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    private UserModel getUser(String userId) {
        UserModel user = userProvider.getUser(userId);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateExtProfile(@Context final HttpServletRequest httpServletRequest,
                                     final ExtProfileRepresentation extProfile) {
        String username = new SSOContext(httpServletRequest).getUsername();

        UserModel user = userProvider.getByUsername(username);
        if (user == null) {
            throw new NotFoundException();
        }

        // Offline token
        UserDataAttributesRepresentation attributes = extProfile.getData().getAttributes();

        if (attributes.getRefreshToken() != null) {
            String offlineToken = attributes.getRefreshToken();
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
        Boolean registrationCompleted = attributes.getRegistrationCompleted();
        if (registrationCompleted != null) {
            user.setRegistrationCompleted(registrationCompleted);
        }

        return Response.ok(ResponseFactory.response(modelToRepresentation.toRepresentation(user))).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseRepresentation getUsers(@QueryParam("filter[username]") String usernameFilter) {
        QueryModel.Builder builder = QueryModel.builder();

        if (usernameFilter != null) {
            builder.addFilter(UserModel.USERNAME, usernameFilter);
        }

        return ResponseFactory.response(userProvider.getUsers(builder.build()).stream()
                .map(f -> modelToRepresentation.toRepresentation(f))
                .collect(Collectors.toList()));
    }

//    @GET
//    @Path("/{userId}/repositories")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<RepositoryRepresentation> getRepositories(@PathParam("userId") String userId) {
//        UserModel user = getUser(userId);
//
//        return user.getRepositories().stream()
//                .map(f -> modelToRepresentation.toRepresentation(f))
//                .collect(Collectors.toList());
//    }
//
//    @POST
//    @Path("/{userId}/repositories")
//    @Produces(MediaType.APPLICATION_JSON)
//    public void refreshRepositories(@PathParam("userId") String userId) {
//        UserModel user = getUser(userId);
//
//        userManager.refreshUserAvailableRepositories(user);
//        userManager.syncUserRepositories(user);
//    }
//
//    /**
//     * Search spaces from user and get back it to you.
//     *
//     * @return spaces from user
//     */
//    @GET
//    @Path("/{userId}/spaces")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<SpaceRepresentation> getSpaces(@PathParam("userId") String userId) {
//        UserModel user = getUser(userId);
//
//        Stream<SpaceRepresentation> sharedSpaces = user.getSharedSpaces().stream()
//                .map(f -> modelToRepresentation.toRepresentation(f));
//        Stream<SpaceRepresentation> ownedSpaces = user.getOwnedSpaces().stream()
//                .map(f -> modelToRepresentation.toRepresentation(f, true));
//
//        return Stream.concat(ownedSpaces, sharedSpaces).collect(Collectors.toList());
//    }
//
//    @POST
//    @Path("/{userId}/spaces")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response createSpace(@PathParam("userId") String userId, final SpaceRepresentation rep) {
//        UserModel user = getUser(userId);
//
//        SpaceModel space = spaceProvider.getByAssignedId(rep.getAssignedId());
//        if (space == null) {
//            // Claim space
//            space = spaceProvider.addSpace(rep.getAssignedId(), user);
//        } else {
//            space.requestAccess(user, new HashSet<>(Collections.singletonList(PermissionType.READ)));
//        }
//
//        URI location = uriInfo.getBaseUriBuilder().path(space.getId()).build();
//        logger.debugv("space claimed success, sending back: {0}", location.toString());
//
//        return Response.status(Response.Status.CREATED).entity(modelToRepresentation.toRepresentation(space, false)).build();
//    }

}