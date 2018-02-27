package org.clarksnut.services.resources;

import org.clarksnut.models.*;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.SpaceRepresentation;
import org.clarksnut.representations.idm.TypedGenericDataRepresentation;
import org.clarksnut.representations.idm.UserRepresentation;
import org.clarksnut.services.ErrorResponse;
import org.clarksnut.services.ErrorResponseException;
import org.clarksnut.services.resources.utils.PATCH;
import org.clarksnut.utils.ModelToRepresentation;

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
@Path("spaces")
@Consumes(MediaType.APPLICATION_JSON)
public class SpacesService extends AbstractResource {

    @Context
    private UriInfo uriInfo;

    @Inject
    private SpaceProvider spaceProvider;

    @Inject
    private UserProvider userProvider;

    @Inject
    private RequestProvider requestProvider;

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
    public GenericDataRepresentation getSpaces(
            @QueryParam("assignedId") String assignedId,
            @QueryParam("q") @DefaultValue("*") String searchText,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("10") int limit,
            @Context HttpServletRequest httpServletRequest) {
        UserModel user = getUserSession(httpServletRequest);

        if (assignedId != null) {
            SpaceModel space = spaceProvider.getByAssignedId(assignedId);
            UserModel spaceOwner = space.getOwner();

            SpaceRepresentation.Data spaceData = modelToRepresentation.toRepresentation(space, uriInfo, user.equals(spaceOwner));
            return new GenericDataRepresentation<>(Collections.singletonList(spaceData));
        }

        if (searchText != null) {
            if (searchText.equals("*")) {
                searchText = "";
            }

            List<SpaceRepresentation.Data> spacesData = spaceProvider.getSpaces(searchText, offset, limit)
                    .stream()
                    .map(f -> {
                        UserModel spaceOwner = f.getOwner();
                        return modelToRepresentation.toRepresentation(f, uriInfo, user.equals(spaceOwner));
                    })
                    .collect(Collectors.toList());

            return new GenericDataRepresentation<>(spacesData);
        }

        throw new BadRequestException();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
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
    @Path("{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public SpaceRepresentation getSpace(
            @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest httpServletRequest) {
        UserModel user = getUserSession(httpServletRequest);

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        return modelToRepresentation.toRepresentation(space, uriInfo, user.equals(spaceOwner)).toSpaceRepresentation();
    }

    @PUT
    @Path("{spaceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public SpaceRepresentation updateSpace(
            @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest httpServletRequest,
            final SpaceRepresentation spaceRepresentation) {
        UserModel user = getUserSession(httpServletRequest);

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        if (!user.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        SpaceRepresentation.Data data = spaceRepresentation.getData();
        SpaceRepresentation.Attributes attributes = data.getAttributes();
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
    public void deleteSpace(
            @PathParam("spaceId") String spaceId,
            @Context HttpServletRequest httpServletRequest) {
        UserModel user = getUserSession(httpServletRequest);

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        if (!user.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        spaceProvider.removeSpace(space);
    }

    @GET
    @Path("{spaceId}/collaborators")
    @Produces(MediaType.APPLICATION_JSON)
    public GenericDataRepresentation getSpaceCollaborators(
            @PathParam("spaceId") String spaceId,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("10") int limit,
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
            @Context HttpServletRequest httpServletRequest,
            final TypedGenericDataRepresentation<List<UserRepresentation.Data>> representation) {
        UserModel user = getUserSession(httpServletRequest);

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();

        if (!user.equals(spaceOwner)) {
            throw new ForbiddenException();
        }

        for (UserRepresentation.Data data : representation.getData()) {
            UserModel collaborator = userProvider.getUserByUsername(data.getAttributes().getUsername());
            space.addCollaborators(collaborator);
        }
    }

    @DELETE
    @Path("{spaceId}/collaborators/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSpaceCollaborators(
            @PathParam("spaceId") String spaceId,
            @PathParam("userId") String userId,
            @Context HttpServletRequest httpServletRequest) throws ErrorResponseException {
        UserModel user = getUserSession(httpServletRequest);

        SpaceModel space = getSpaceById(spaceId);
        UserModel spaceOwner = space.getOwner();
        List<UserModel> collaborators = space.getCollaborators();

        if (!user.equals(spaceOwner) && !collaborators.contains(user)) {
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
