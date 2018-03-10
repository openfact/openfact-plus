package org.clarksnut.services.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.clarksnut.models.*;
import org.clarksnut.representations.idm.*;
import org.clarksnut.services.ErrorResponse;
import org.clarksnut.services.ErrorResponseException;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Users", description = "Users Spaces REST API", consumes = "application/json")
public class UsersSpacesService extends AbstractResource {

    private static final Logger logger = Logger.getLogger(UsersSpacesService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private RequestProvider requestProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    /**
     * Spaces
     */

    @GET
    @Path("/{userId}/spaces")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return allowed Spaces of User")
    public GenericDataRepresentation<List<SpaceRepresentation.SpaceData>> getUserSpaces(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Role", allowableValues = "owner, collaborator") @QueryParam("role") @DefaultValue("owner") String role,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max results") @QueryParam("limit") @DefaultValue("10") int limit,
            @Context final HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;
        boolean fullInfo = false;

        if (userId.equals("me")) {
            user = getUserSession(request);
            fullInfo = true;
        } else {
            user = getUserById(userId);
        }

        int totalCount;
        List<SpaceModel> spaces;

        // Search
        PermissionType permissionType = PermissionType.valueOf(role.toUpperCase());
        switch (permissionType) {
            case OWNER:
                spaces = user.getOwnedSpaces(offset, limit + 1);
                totalCount = user.getOwnedSpaces().size();
                break;
            case COLLABORATOR:
                spaces = user.getCollaboratedSpaces(offset, limit + 1);
                totalCount = user.getCollaboratedSpaces().size();
                break;
            default:
                throw new ErrorResponseException("Invalid Role", Response.Status.BAD_REQUEST);
        }

        // Metadata
        Map<String, Object> meta = new HashMap<>();
        meta.put("totalCount", totalCount);

        // Links
        Map<String, String> links = new HashMap<>();
        links.put("first", uriInfo.getBaseUriBuilder()
                .path(UsersSpacesService.class)
                .path(UsersSpacesService.class, "getUserSpaces")
                .build(userId).toString() +
                "?role=" + role +
                "?offset=0" +
                "&limit=" + limit);

        links.put("last", uriInfo.getBaseUriBuilder()
                .path(UsersSpacesService.class)
                .path(UsersSpacesService.class, "getUserSpaces")
                .build(userId).toString() +
                "?role=" + role +
                "?offset=" + (totalCount > 0 ? (((totalCount - 1) % limit) * limit) : 0) +
                "&limit=" + limit);

        if (spaces.size() > limit) {
            links.put("next", uriInfo.getBaseUriBuilder()
                    .path(UsersSpacesService.class)
                    .path(UsersSpacesService.class, "getUserSpaces")
                    .build(userId).toString() +
                    "?role=" + role +
                    "?offset=" + (offset + limit) +
                    "&limit=" + limit);

            // Remove last item
            spaces.remove(spaces.size() - 1);
        }


        final boolean isFullInfo = fullInfo;
        List<SpaceRepresentation.SpaceData> spacesData = spaces.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, isFullInfo))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(spacesData, links, meta);
    }

    @GET
    @Path("/{userId}/spaces/{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Space")
    public SpaceRepresentation getUserSpace(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;
        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED); // Use /spaces/{spaceId} instead
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        boolean fullInfo = false;
        if (user.equals(spaceOwner)) {
            fullInfo = true;
        }

        return modelToRepresentation.toRepresentation(space, uriInfo, fullInfo).toSpaceRepresentation();
    }

    @PUT
    @Path("/{userId}/spaces/{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update space")
    public SpaceRepresentation updateUserSpace(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest httpServletRequest,
            final SpaceRepresentation spaceRepresentation
    ) throws ErrorResponseException {
        UserModel user;
        if (userId.equals("me")) {
            user = getUserSession(httpServletRequest);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        if (!user.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        SpaceRepresentation.SpaceData data = spaceRepresentation.getData();
        SpaceRepresentation.SpaceAttributes attributes = data.getAttributes();
        if (attributes.getName() != null) {
            space.setName(attributes.getName());
        }
        if (attributes.getDescription() != null) {
            space.setDescription(attributes.getDescription());
        }

        return modelToRepresentation.toRepresentation(space, uriInfo, true).toSpaceRepresentation();
    }

    @DELETE
    @Path("/{userId}/spaces/{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Delete space")
    public void deleteUserSpace(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        if (!user.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        spaceProvider.removeSpace(space);
    }

    /**
     * Space Collaborators
     */

    @GET
    @Path("/{userId}/spaces/{spaceId}/collaborators")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Collaborators")
    public GenericDataRepresentation<List<UserRepresentation.UserData>> getUserSpaceCollaborators(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max results") @QueryParam("limit") @DefaultValue("10") int limit,
            @Context HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        if (!user.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        List<UserModel> collaborators = space.getCollaborators(offset, limit + 1);
        int totalCount = space.getCollaborators().size();

        // Metadata
        Map<String, Object> meta = new HashMap<>();
        meta.put("totalCount", totalCount);

        // Links
        Map<String, String> links = new HashMap<>();
        links.put("first", uriInfo.getBaseUriBuilder()
                .path(SpacesService.class)
                .path(SpacesService.class, "getUserSpaceCollaborators")
                .build(spaceId).toString() +
                "?offset=0" +
                "&limit=" + limit);

        links.put("last", uriInfo.getBaseUriBuilder()
                .path(SpacesService.class)
                .path(SpacesService.class, "getUserSpaceCollaborators")
                .build(spaceId).toString() +
                "?offset=" + (totalCount > 0 ? (((totalCount - 1) % limit) * limit) : 0) +
                "&limit=" + limit);

        if (collaborators.size() > limit) {
            links.put("next", uriInfo.getBaseUriBuilder()
                    .path(SpacesService.class)
                    .path(SpacesService.class, "getUserSpaceCollaborators")
                    .build(spaceId).toString() +
                    "?offset=" + (offset + limit) +
                    "&limit=" + limit);

            // Remove last item
            collaborators.remove(collaborators.size() - 1);
        }

        return new GenericDataRepresentation<>(collaborators.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, false))
                .collect(Collectors.toList()), links, meta);
    }

    @POST
    @Path("/{userId}/spaces/{spaceId}/collaborators")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Add new Collaborator")
    public void addSpaceCollaborators(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest request,
            final TypedGenericDataRepresentation<List<UserRepresentation.UserData>> representation
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();
        List<UserModel> currentCollaborators = space.getCollaborators();

        if (!user.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        for (UserRepresentation.UserData data : representation.getData()) {
            UserModel newCollaborator = userProvider.getUserByUsername(data.getAttributes().getUsername());
            if (!currentCollaborators.contains(newCollaborator)) {
                space.addCollaborators(newCollaborator);
            } else {
                throw new ErrorResponseException("Collaborator already registered", Response.Status.BAD_REQUEST);
            }
        }
    }

    @DELETE
    @Path("/{userId}/spaces/{spaceId}/collaborators/{collaboratorId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Remove Collaborator")
    public Response removeSpaceCollaborators(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @ApiParam(value = "User Id") @PathParam("collaboratorId") String collaboratorId,
            @Context HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();
        List<UserModel> collaborators = space.getCollaborators();

        if (!user.equals(spaceOwner) && !collaborators.contains(user)) {
            throw new ForbiddenException();
        }


        UserModel collaborator = userProvider.getUser(collaboratorId);
        if (spaceOwner.equals(collaborator)) {
            return ErrorResponse.error("Could not delete the owner", Response.Status.BAD_REQUEST);
        }
        space.removeCollaborators(collaborator);
        return Response.ok().build();
    }

    /*
     * Space request access
     */

    @GET
    @Path("/{userId}/spaces/{spaceId}/request-access")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Notifications")
    public GenericDataRepresentation<List<RequestRepresentation.RequestData>> getUserSpaceRequestAccess(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @ApiParam(value = "Status", allowableValues = "pending, accepted, rejected") @DefaultValue("pending") @QueryParam("status") String status,
            @Context final HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();
        if (!user.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        RequestStatus requestStatus = RequestStatus.valueOf(status.toUpperCase());

        List<RequestRepresentation.RequestData> requests = requestProvider.getRequests(requestStatus, new SpaceModel[]{space})
                .stream()
                .map(f -> modelToRepresentation.toRepresentation(f))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(requests);
    }

}