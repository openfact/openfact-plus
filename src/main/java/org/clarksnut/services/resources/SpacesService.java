package org.clarksnut.services.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.SpaceRepresentation;
import org.clarksnut.services.ErrorResponseException;
import org.clarksnut.utils.ModelToRepresentation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
@Path("/api/spaces")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Spaces", description = "Spaces REST API", consumes = "application/json")
public class SpacesService extends AbstractResource {

    @Context
    private UriInfo uriInfo;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create new Space")
    public Response createSpace(
            final SpaceRepresentation spaceRepresentation
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

        SpaceModel newSpace = spaceProvider.addSpace(owner, attributes.getAssignedId(), attributes.getName());
        newSpace.setDescription(attributes.getDescription());

        // Result
        SpaceRepresentation.SpaceData createdSpaceRepresentation = modelToRepresentation.toRepresentation(newSpace, uriInfo, true);
        return Response.status(Response.Status.CREATED).entity(createdSpaceRepresentation.toSpaceRepresentation()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Spaces")
    public GenericDataRepresentation<List<SpaceRepresentation.SpaceData>> getSpaces(
            @ApiParam(value = "Space Assigned Id") @QueryParam("assignedId") String assignedId,
            @ApiParam(value = "Filter Text") @QueryParam("filterText") String filterText,
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

        Optional<String> filterTextOptional = Optional.ofNullable(filterText);
        List<SpaceRepresentation.SpaceData> spacesData = spaceProvider.getSpaces(filterTextOptional.orElse(""), offset, limit).stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, false))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(spacesData);
    }

    @GET
    @Path("{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Space")
    public SpaceRepresentation getSpace(
            @ApiParam(value = "Space Id") @PathParam("spaceId") String spaceId
    ) {
        SpaceModel space = getSpaceById(spaceId);
        return modelToRepresentation.toRepresentation(space, uriInfo, false).toSpaceRepresentation();
    }


}
