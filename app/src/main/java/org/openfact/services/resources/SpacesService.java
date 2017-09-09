package org.openfact.services.resources;

import org.openfact.models.SpaceModel;
import org.openfact.models.SpaceProvider;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.models.utils.ModelToRepresentation;
import org.openfact.representation.idm.SpaceRepresentation;
import org.openfact.services.ErrorResponseException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Stateless
@Path("spaces")
@Consumes(MediaType.APPLICATION_JSON)
public class SpacesService {

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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSpace(final SpaceRepresentation spaceRepresentation) throws ErrorResponseException {
        SpaceRepresentation.Data data = spaceRepresentation.getData();
        SpaceRepresentation.Attributes attributes = data.getAttributes();
        SpaceRepresentation.Relationships relationships = data.getRelationships();

        SpaceRepresentation.OwnedBy ownedBy = relationships.getOwnedBy();
        UserModel user = userProvider.getUserByIdentityID(ownedBy.getData().getId());

        // Create space
        if (spaceProvider.getByAssignedId(attributes.getAssignedId()) != null) {
            throw new ErrorResponseException("Space already exists", Response.Status.CONFLICT);
        }

        SpaceModel space = spaceProvider.addSpace(attributes.getAssignedId(), attributes.getName(), user);
        space.setDescription(attributes.getDescription());

        SpaceRepresentation.Data createdSpaceRep = modelToRepresentation.toRepresentation(space, uriInfo);
        return Response.status(Response.Status.CREATED).entity(createdSpaceRep.toSpaceRepresentation()).build();
    }

    @GET
    @Path("{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public SpaceRepresentation getSpace(@PathParam("spaceId") String spaceId) {
        SpaceModel space = getSpaceById(spaceId);
        return modelToRepresentation.toRepresentation(space, uriInfo).toSpaceRepresentation();
    }

    @DELETE
    @Path("{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteSpace(@PathParam("spaceId") String spaceId) {
        SpaceModel space = getSpaceById(spaceId);
        spaceProvider.removeSpace(space);
    }

}
