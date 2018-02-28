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
import org.clarksnut.services.resources.utils.PATCH;
import org.clarksnut.utils.ModelToRepresentation;
import org.wildfly.swarm.swagger.SwaggerConfig;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Path("/api/spaces")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Spaces", consumes = "application/json")
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create Space", notes = "This will create a space. [user] role required")
    public Response createSpace(final SpaceRepresentation spaceRepresentation) throws ErrorResponseException {
        SpaceRepresentation.Data data = spaceRepresentation.getData();
        SpaceRepresentation.Attributes attributes = data.getAttributes();
        SpaceRepresentation.Relationships relationships = data.getRelationships();

        SpaceRepresentation.OwnedBy ownedBy = relationships.getOwnedBy();
        UserModel owner = userProvider.getUser(ownedBy.getData().getId());

        // Create space
        if (spaceProvider.getByAssignedId(attributes.getAssignedId()) != null) {
            throw new ErrorResponseException("Space already exists", Response.Status.CONFLICT);
        }

        SpaceModel space = spaceProvider.addSpace(owner, attributes.getAssignedId(), attributes.getName());
        space.setDescription(attributes.getDescription());

        SpaceRepresentation.Data createdSpaceRepresentation = modelToRepresentation.toRepresentation(space, uriInfo, true);
        return Response.status(Response.Status.CREATED).entity(createdSpaceRepresentation.toSpaceRepresentation()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Spaces", notes = "This will search spaces. [view-spaces] role required")
    public GenericDataRepresentation<List<SpaceRepresentation.Data>> getSpaces(
            @ApiParam(value = "Space Assigned Id") @QueryParam("assignedId") String assignedId,
            @ApiParam(value = "Full text search value") @QueryParam("q") @DefaultValue("*") String searchText,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max results") @QueryParam("limit") @DefaultValue("10") int limit,
            @Context HttpServletRequest httpServletRequest) {
        if (assignedId != null) {
            SpaceModel space = spaceProvider.getByAssignedId(assignedId);

            SpaceRepresentation.Data spaceData = modelToRepresentation.toRepresentation(space, uriInfo, false);
            return new GenericDataRepresentation<>(Collections.singletonList(spaceData));
        }

        if (searchText != null) {
            if (searchText.equals("*")) {
                searchText = "";
            }
        }

        List<SpaceRepresentation.Data> spacesData = spaceProvider.getSpaces(searchText, offset, limit).stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, false))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(spacesData);
    }

    @GET
    @Path("{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Space", notes = "This will get a space. [view-spaces] role required")
    public SpaceRepresentation getSpace(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest httpServletRequest) {
        SpaceModel space = getSpaceById(spaceId);
        return modelToRepresentation.toRepresentation(space, uriInfo, false).toSpaceRepresentation();
    }

}
