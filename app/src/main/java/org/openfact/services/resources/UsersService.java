package org.openfact.services.resources;

import org.jboss.logging.Logger;
import org.openfact.models.*;
import org.openfact.models.utils.ModelToRepresentation;
import org.openfact.representation.idm.*;
import org.openfact.services.managers.UserManager;

import javax.ejb.Stateless;
import javax.inject.Inject;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseRepresentation getUsers(@QueryParam("filter[username]") String usernameFilter) {
        QueryModel.Builder builder = QueryModel.builder();

        if (usernameFilter != null) {
            builder.addFilter(UserModel.USERNAME, usernameFilter);
        }

        return ResponseFactory.response(ModelType.USER, userProvider.getUsers(builder.build()).stream()
                .map(f -> modelToRepresentation.toRepresentation(f))
                .collect(Collectors.toList()));
    }

    @GET
    @Path("/{userId}/repositories")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RepositoryRepresentation> getRepositories(@PathParam("userId") String userId) {
        UserModel user = getUser(userId);

        return user.getRepositories().stream()
                .map(f -> modelToRepresentation.toRepresentation(f))
                .collect(Collectors.toList());
    }

    @POST
    @Path("/{userId}/repositories")
    @Produces(MediaType.APPLICATION_JSON)
    public void refreshRepositories(@PathParam("userId") String userId) {
        UserModel user = getUser(userId);

        userManager.refreshUserAvailableRepositories(user);
        userManager.syncUserRepositories(user);
    }

    /**
     * Search spaces from user and get back it to you.
     *
     * @return spaces from user
     */
    @GET
    @Path("/{userId}/spaces")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SpaceRepresentation> getSpaces(@PathParam("userId") String userId) {
        UserModel user = getUser(userId);

        Stream<SpaceRepresentation> sharedSpaces = user.getSharedSpaces().stream()
                .map(f -> modelToRepresentation.toRepresentation(f));
        Stream<SpaceRepresentation> ownedSpaces = user.getOwnedSpaces().stream()
                .map(f -> modelToRepresentation.toRepresentation(f, true));

        return Stream.concat(ownedSpaces, sharedSpaces).collect(Collectors.toList());
    }

    @POST
    @Path("/{userId}/spaces")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSpace(@PathParam("userId") String userId, final SpaceRepresentation rep) {
        UserModel user = getUser(userId);

        SpaceModel space = spaceProvider.getByAssignedId(rep.getAssignedId());
        if (space == null) {
            // Claim space
            space = spaceProvider.addSpace(rep.getAssignedId(), user);
        } else {
            space.requestAccess(user, new HashSet<>(Collections.singletonList(PermissionType.READ)));
        }

        URI location = uriInfo.getBaseUriBuilder().path(space.getId()).build();
        logger.debugv("space claimed success, sending back: {0}", location.toString());

        return Response.status(Response.Status.CREATED).entity(modelToRepresentation.toRepresentation(space, false)).build();
    }

}