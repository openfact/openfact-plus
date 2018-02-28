package org.clarksnut.services.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.clarksnut.models.*;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.SpaceRepresentation;
import org.clarksnut.representations.idm.TypedGenericDataRepresentation;
import org.clarksnut.representations.idm.UserRepresentation;
import org.clarksnut.services.ErrorResponse;
import org.clarksnut.services.ErrorResponseException;
import org.clarksnut.utils.ModelToRepresentation;

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
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@Path("/api/namespaces")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Namespaces", consumes = "application/json")
public class NamespacesService extends AbstractResource {

    @Context
    private UriInfo uriInfo;

    @Inject
    private SpaceProvider spaceProvider;

    @Inject
    private UserProvider userProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    private SpaceModel getSpaceById(String spaceId) {
        SpaceModel space = spaceProvider.getSpace(spaceId);
        if (space == null) {
            throw new NotFoundException();
        }
        return space;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Spaces of user", notes = "This will search owned and collaborated spaces. [user] role required")
    public GenericDataRepresentation<List<SpaceRepresentation.SpaceData>> getUserSpaces(
            @ApiParam(value = "Role", allowableValues = "owner, collaborator") @QueryParam("role") @DefaultValue("owner") String role,
            @Context HttpServletRequest httpServletRequest) throws ErrorResponseException {
        UserModel sessionUser = getUserSession(httpServletRequest);

        Set<SpaceModel> spaces = null;
        PermissionType permissionType = PermissionType.valueOf(role.toUpperCase());
        switch (permissionType) {
            case OWNER:
                spaces = sessionUser.getOwnedSpaces();
                break;
            case COLLABORATOR:
                spaces = sessionUser.getCollaboratedSpaces();
                break;
            default:
                throw new ErrorResponseException("Invalid Role", Response.Status.BAD_REQUEST);
        }

        List<SpaceRepresentation.SpaceData> spacesData = spaces.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, true))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(spacesData);
    }

    @GET
    @Path("{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Space", notes = "This will get a space, just the owner will be allowed. [user] role required")
    public SpaceRepresentation getUserSpace(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest httpServletRequest) {
        UserModel sessionUser = getUserSession(httpServletRequest);

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        if (!sessionUser.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        return modelToRepresentation.toRepresentation(space, uriInfo, true).toSpaceRepresentation();
    }


    @PUT
    @Path("{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update space", notes = "Accessed just by the owner, the owner is identified by current token. [user] role required")
    public SpaceRepresentation updateUserSpace(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest httpServletRequest,
            final SpaceRepresentation spaceRepresentation) {
        UserModel sessionUser = getUserSession(httpServletRequest);

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        if (!sessionUser.equals(spaceOwner)) {
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
    @Path("{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Delete space", notes = "Accessed just by the owner, the owner is identified by current token. [user] role required")
    public void deleteUserSpace(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest httpServletRequest) {
        UserModel sessionUser = getUserSession(httpServletRequest);

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        if (!sessionUser.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        spaceProvider.removeSpace(space);
    }

    @GET
    @Path("{spaceId}/collaborators")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Space SpaceCollaborators", notes = "Accessed just by the owner, the owner is identified by current token. [user] role required")
    public GenericDataRepresentation<List<UserRepresentation.UserData>> getSpaceCollaborators(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max results") @QueryParam("limit") @DefaultValue("10") int limit,
            @Context HttpServletRequest httpServletRequest) {
        UserModel user = getUserSession(httpServletRequest);

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
                .path(NamespacesService.class)
                .path(NamespacesService.class, "getSpaceCollaborators")
                .build(spaceId).toString() +
                "?offset=0" +
                "&limit=" + limit);

        links.put("last", uriInfo.getBaseUriBuilder()
                .path(NamespacesService.class)
                .path(NamespacesService.class, "getSpaceCollaborators")
                .build(spaceId).toString() +
                "?offset=" + (totalCount > 0 ? (((totalCount - 1) % limit) * limit) : 0) +
                "&limit=" + limit);

        if (collaborators.size() > limit) {
            links.put("next", uriInfo.getBaseUriBuilder()
                    .path(NamespacesService.class)
                    .path(NamespacesService.class, "getSpaceCollaborators")
                    .build(spaceId).toString() +
                    "?offset=" + (offset + limit) +
                    "&limit=" + limit);

            // Remove last item
            collaborators.remove(links.size() - 1);
        }

        return new GenericDataRepresentation<>(collaborators.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, false))
                .collect(Collectors.toList()), links, meta);
    }

    @POST
    @Path("{spaceId}/collaborators")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Add Space SpaceCollaborators", notes = "Accessed just by the owner, the owner is identified by current token. [user] role required")
    public void addSpaceCollaborators(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest httpServletRequest,
            final TypedGenericDataRepresentation<List<UserRepresentation.UserData>> representation) throws ErrorResponseException {
        UserModel sessionUser = getUserSession(httpServletRequest);

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();
        List<UserModel> currentCollaborators = space.getCollaborators();

        if (!sessionUser.equals(spaceOwner)) {
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
    @Path("{spaceId}/collaborators/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Remove Space SpaceCollaborators", notes = "Accessed just by the owner, the owner is identified by current token. [user] role required")
    public Response removeSpaceCollaborators(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @Context HttpServletRequest httpServletRequest) throws ErrorResponseException {
        UserModel sessionUser = getUserSession(httpServletRequest);

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();
        List<UserModel> collaborators = space.getCollaborators();

        if (!sessionUser.equals(spaceOwner) && !collaborators.contains(sessionUser)) {
            throw new ForbiddenException();
        }


        UserModel collaborator = userProvider.getUser(userId);
        if (spaceOwner.equals(collaborator)) {
            return ErrorResponse.error("Could not delete the owner", Response.Status.BAD_REQUEST);
        }
        space.removeCollaborators(collaborator);
        return Response.ok().build();
    }

}
