package org.clarksnut.services.resources;

import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.SpaceProvider;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.SpaceRepresentation;
import org.clarksnut.utils.ModelToRepresentation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
@Path("namedspaces")
@Consumes(MediaType.APPLICATION_JSON)
public class NamedSpacesService {

    @Context
    private UriInfo uriInfo;

    @Inject
    private UserProvider userProvider;

    @Inject
    private SpaceProvider spaceProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    private UserModel getUserByUsername(String username) {
        UserModel user = userProvider.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    private SpaceModel getSpaceByAssignedID(String assignedID) {
        SpaceModel space = spaceProvider.getByAssignedId(assignedID);
        if (space == null) {
            throw new NotFoundException();
        }
        return space;
    }

    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public GenericDataRepresentation getSpacesByUser(
            @PathParam("username") String username,
            @QueryParam("page[offset]") Integer offset,
            @QueryParam("page[limit]") Integer limit) {
        UserModel user = getUserByUsername(username);

        if (offset == null) {
            offset = 0;
        }
        if (limit == null) {
            limit = 100;
        }

        List<SpaceModel> spaces = spaceProvider.getSpaces(user, offset, limit + 1);

        // Meta
        int totalCount = spaceProvider.countSpaces(user);

        Map<String, Object> meta = new HashMap<>();
        meta.put("totalCount", totalCount);

        // Links
        Map<String, String> links = new HashMap<>();
        links.put("first", uriInfo.getBaseUriBuilder()
                .path(NamedSpacesService.class)
                .path(NamedSpacesService.class, "getSpacesByUser")
                .build(username).toString() +
                "?page[offset]=0" +
                "&page[limit]=" + limit);

        links.put("last", uriInfo.getBaseUriBuilder()
                .path(NamedSpacesService.class)
                .path(NamedSpacesService.class, "getSpacesByUser")
                .build(username).toString() +
                "?page[offset]=" + (totalCount > 0 ? (((totalCount - 1) % limit) * limit) : 0) +
                "&page[limit]=" + limit);

        if (spaces.size() > limit) {
            links.put("next", uriInfo.getBaseUriBuilder()
                    .path(NamedSpacesService.class)
                    .path(NamedSpacesService.class, "getSpacesByUser")
                    .build(username).toString() +
                    "?page[offset]=" + (offset + limit) +
                    "&page[limit]=" + limit);

            // Remove last item
            spaces.remove(links.size() - 1);
        }

        return new GenericDataRepresentation<>(spaces.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList()), links, meta);
    }

    @GET
    @Path("{username}/{assignedID}")
    @Produces(MediaType.APPLICATION_JSON)
    public SpaceRepresentation getSpaceByUser(
            @PathParam("username") String username,
            @PathParam("assignedID") String assignedID) {
        UserModel user = getUserByUsername(username);
        SpaceModel space = getSpaceByAssignedID(assignedID);

        // TODO check if user has access to space
        if (space.getOwner().equals(user) || user.getCollaboratedSpaces().contains(space)) {
            return modelToRepresentation.toRepresentation(space, uriInfo).toSpaceRepresentation();
        }

        throw new BadRequestException();
    }

}
