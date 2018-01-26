package org.clarksnut.services.resources;

import org.clarksnut.models.*;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.SpaceRepresentation;
import org.clarksnut.representations.idm.TypedGenericDataRepresentation;
import org.clarksnut.representations.idm.UserRepresentation;
import org.clarksnut.services.ErrorResponseException;
import org.clarksnut.services.resources.utils.PATCH;
import org.clarksnut.utils.ModelToRepresentation;

import javax.ejb.Stateless;
import javax.inject.Inject;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GenericDataRepresentation getSpaces(@QueryParam("q") String filterText,
                                               @QueryParam("assignedId") String assignedId) {
        QueryModel.Builder queryBuilder = QueryModel.builder();

        if (filterText != null) {
            queryBuilder.filterText(filterText);
        }
        if (assignedId != null) {
            queryBuilder.addFilter(SpaceModel.ASSIGNED_ID, assignedId);
        }

        List<SpaceRepresentation.Data> data = spaceProvider.getSpaces(queryBuilder.build()).stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(data);
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
        SpaceModel space = spaceProvider.getByAssignedId(attributes.getAssignedId());
        if (space != null) {
            throw new ErrorResponseException("Space already exists", Response.Status.CONFLICT);
        }

        space = spaceProvider.addSpace(attributes.getAssignedId(), attributes.getName(), user);
        space.setDescription(attributes.getDescription());

        SpaceRepresentation.Data createdSpaceRepresentation = modelToRepresentation.toRepresentation(space, uriInfo);
        return Response.status(Response.Status.CREATED).entity(createdSpaceRepresentation.toSpaceRepresentation()).build();
    }

    @GET
    @Path("{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public SpaceRepresentation getSpace(@PathParam("spaceId") String spaceId) {
        SpaceModel space = getSpaceById(spaceId);
        return modelToRepresentation.toRepresentation(space, uriInfo).toSpaceRepresentation();
    }

    @PATCH
    @Path("{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public SpaceRepresentation updateSpace(@PathParam("spaceId") String spaceId, final SpaceRepresentation spaceRepresentation) {
        SpaceModel space = getSpaceById(spaceId);

        SpaceRepresentation.Data data = spaceRepresentation.getData();
        SpaceRepresentation.Attributes attributes = data.getAttributes();
        if (attributes.getName() != null) {
            space.setName(attributes.getName());
        }
        if (attributes.getDescription() != null) {
            space.setDescription(attributes.getDescription());
        }

        return modelToRepresentation.toRepresentation(space, uriInfo).toSpaceRepresentation();
    }

    @DELETE
    @Path("{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteSpace(@PathParam("spaceId") String spaceId) {
        SpaceModel space = getSpaceById(spaceId);
        spaceProvider.removeSpace(space);
    }

    @GET
    @Path("{spaceId}/collaborators")
    @Produces(MediaType.APPLICATION_JSON)
    public GenericDataRepresentation getSpaceCollaborators(
            @PathParam("spaceId") String spaceId,
            @QueryParam("page[offset]") Integer offset,
            @QueryParam("page[limit]") Integer limit) {

        if (offset == null) {
            offset = 0;
        }
        if (limit == null) {
            limit = 100;
        }

        SpaceModel space = getSpaceById(spaceId);

        List<UserModel> collaborators = space.getCollaborators(offset, limit + 1);

        // Meta
        int totalCount = space.getCollaborators().size();

        Map<String, Object> meta = new HashMap<>();
        meta.put("totalCount", totalCount);

        // Links
        Map<String, String> links = new HashMap<>();
        links.put("first", uriInfo.getBaseUriBuilder()
                .path(SpacesService.class)
                .path(SpacesService.class, "getSpaceCollaborators")
                .build(spaceId).toString() +
                "?page[offset]=0" +
                "&page[limit]=" + limit);

        links.put("last", uriInfo.getBaseUriBuilder()
                .path(SpacesService.class)
                .path(SpacesService.class, "getSpaceCollaborators")
                .build(spaceId).toString() +
                "?page[offset]=" + (totalCount > 0 ? (((totalCount - 1) % limit) * limit) : 0) +
                "&page[limit]=" + limit);

        if (collaborators.size() > limit) {
            links.put("next", uriInfo.getBaseUriBuilder()
                    .path(SpacesService.class)
                    .path(SpacesService.class, "getSpaceCollaborators")
                    .build(spaceId).toString() +
                    "?page[offset]=" + (offset + limit) +
                    "&page[limit]=" + limit);

            // Remove last item
            collaborators.remove(links.size() - 1);
        }

        return new GenericDataRepresentation<>(collaborators.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList()), links, meta);
    }

    @POST
    @Path("{spaceId}/collaborators")
    @Produces(MediaType.APPLICATION_JSON)
    public void addSpaceCollaborators(
            @PathParam("spaceId") String spaceId,
            final TypedGenericDataRepresentation<List<UserRepresentation.Data>> representation) {
        SpaceModel space = getSpaceById(spaceId);

        for (UserRepresentation.Data data : representation.getData()) {
            UserModel user = userProvider.getUserByIdentityID(data.getId());
            space.addCollaborators(user);
        }
    }

    @DELETE
    @Path("{spaceId}/collaborators/{identityID}")
    @Produces(MediaType.APPLICATION_JSON)
    public void removeSpaceCollaborators(
            @PathParam("spaceId") String spaceId,
            @PathParam("identityID") String identityID) {
        SpaceModel space = getSpaceById(spaceId);
        UserModel user = userProvider.getUserByIdentityID(identityID);
        boolean result = space.removeCollaborators(user);
        if (!result) {
            throw new InternalServerErrorException();
        }
    }

}
