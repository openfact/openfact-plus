package org.clarksnut.services.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.SpaceProvider;
import org.clarksnut.models.UserModel;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
@Path("/api/spaces")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Spaces", description = "Spaces REST API", consumes = "application/json")
public class SpacesService extends AbstractResource {

    @Context
    private UriInfo uriInfo;

    @Inject
    private SpaceProvider spaceProvider;

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
    @ApiOperation(value = "Return list of Spaces")
    public GenericDataRepresentation<List<SpaceRepresentation.SpaceData>> getSpaces(
            @ApiParam(value = "Space Assigned Id") @QueryParam("assignedId") String assignedId,
            @ApiParam(value = "Filter Text") @QueryParam("filterText") @DefaultValue("") String filterText,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max results") @QueryParam("limit") @DefaultValue("10") int limit
    ) {
        if (assignedId != null) {
            SpaceModel space = spaceProvider.getByAssignedId(assignedId);
            if (space == null) {
                return new GenericDataRepresentation<>(Collections.emptyList());
            } else {
                SpaceRepresentation.SpaceData spaceData = modelToRepresentation.toRepresentation(space, uriInfo, false);
                return new GenericDataRepresentation<>(Collections.singletonList(spaceData));
            }
        }

        if (filterText != null) {
            if (filterText.equals("*")) {
                filterText = "";
            }
        }

        List<SpaceRepresentation.SpaceData> spacesData = spaceProvider.getSpaces(filterText, offset, limit).stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, false))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(spacesData);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create new Space")
    public Response createSpace(
            final SpaceRepresentation spaceRepresentation,
            @Context final HttpServletRequest request
    ) throws ErrorResponseException {
        SpaceRepresentation.SpaceData data = spaceRepresentation.getData();
        SpaceRepresentation.SpaceAttributes attributes = data.getAttributes();
        SpaceRepresentation.SpaceRelationships relationships = data.getRelationships();

        SpaceRepresentation.SpaceOwnedBy ownedBy = relationships.getOwnedBy();
        UserModel owner = userProvider.getUser(ownedBy.getData().getId());

        // Create space
        if (spaceProvider.getByAssignedId(attributes.getAssignedId()) != null) {
            throw new ErrorResponseException("Space already exists", Response.Status.CONFLICT);
        }

        // Authz
        SpaceModel newSpace = null;
        try {
            newSpace = spaceProvider.addSpace(owner, attributes.getAssignedId(), attributes.getName());
            newSpace.setDescription(attributes.getDescription());
            createSpaceProtectedResource(newSpace, owner, request);
        } catch (Throwable e) {
            if (newSpace != null) {
                getAuthzClient(request).protection().resource().delete(newSpace.getExternalId());
            }
        }

        // Result
        SpaceRepresentation.SpaceData createdSpaceRepresentation = modelToRepresentation.toRepresentation(newSpace, uriInfo, true);
        return Response.status(Response.Status.CREATED).entity(createdSpaceRepresentation.toSpaceRepresentation()).build();
    }

    @GET
    @Path("{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return one Space")
    public SpaceRepresentation getSpace(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId
    ) {
        SpaceModel space = getSpaceById(spaceId);
        return modelToRepresentation.toRepresentation(space, uriInfo, false).toSpaceRepresentation();
    }

    @PUT
    @Path("/spaces/{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update space")
    public SpaceRepresentation updateSpace(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            final SpaceRepresentation spaceRepresentation
    ) throws ErrorResponseException {
        SpaceModel space = getSpaceById(spaceId);

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
    @Path("/{spaceId}")
    public Response deleteSpace(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest request
    ) {
        SpaceModel space = getSpaceById(spaceId);

        try {
            deleteSpaceProtectedResource(space, request);
            spaceProvider.removeSpace(space);
        } catch (Exception e) {
            throw new RuntimeException("Could not delete album.", e);
        }

        return Response.ok().build();
    }

    @GET
    @Path("{spaceId}/collaborators")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return list of Collaborators")
    public GenericDataRepresentation<List<UserRepresentation.UserData>> getSpaceCollaborators(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max results") @QueryParam("limit") @DefaultValue("10") int limit
    ) {
        SpaceModel space = getSpaceById(spaceId);

        List<UserModel> collaborators = space.getCollaborators(offset, limit + 1);
        int totalCount = space.getCollaborators().size();

        // Metadata
        Map<String, Object> meta = new HashMap<>();
        meta.put("totalCount", totalCount);

        // Links
        Map<String, String> links = new HashMap<>();
        links.put("first", uriInfo.getBaseUriBuilder()
                .path(SpacesService.class)
                .path(SpacesService.class, "getSpaceCollaborators")
                .build(spaceId).toString() +
                "?offset=0" +
                "&limit=" + limit);

        links.put("last", uriInfo.getBaseUriBuilder()
                .path(SpacesService.class)
                .path(SpacesService.class, "getSpaceCollaborators")
                .build(spaceId).toString() +
                "?offset=" + (totalCount > 0 ? (((totalCount - 1) % limit) * limit) : 0) +
                "&limit=" + limit);

        if (collaborators.size() > limit) {
            links.put("next", uriInfo.getBaseUriBuilder()
                    .path(SpacesService.class)
                    .path(SpacesService.class, "getSpaceCollaborators")
                    .build(spaceId).toString() +
                    "?offset=" + (offset + limit) +
                    "&limit=" + limit);

            // Remove last item
            collaborators.remove(collaborators.size() - 1);
        }

        return new GenericDataRepresentation<>(collaborators.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, true))
                .collect(Collectors.toList()), links, meta);
    }

    @POST
    @Path("{spaceId}/collaborators")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Add new Collaborator", notes = "[user] role required")
    public void addSpaceCollaborators(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            final TypedGenericDataRepresentation<List<UserRepresentation.UserData>> representation
    ) throws ErrorResponseException {
        SpaceModel space = getSpaceById(spaceId);
        List<UserModel> currentCollaborators = space.getCollaborators();

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
    @ApiOperation(value = "Remove Collaborator")
    public Response removeSpaceCollaborators(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @ApiParam(value = "User Id") @PathParam("userId") String userId
    ) throws ErrorResponseException {
        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        UserModel collaborator = userProvider.getUser(userId);
        if (spaceOwner.equals(collaborator)) {
            return ErrorResponse.error("Could not delete the owner", Response.Status.BAD_REQUEST);
        }
        space.removeCollaborators(collaborator);
        return Response.ok().build();
    }

}
