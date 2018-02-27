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
import javax.servlet.http.HttpServletRequest;
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
public class NamedSpacesService extends AbstractResource {

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

    private SpaceModel getSpaceByAssignedId(String assignedId) {
        SpaceModel space = spaceProvider.getByAssignedId(assignedId);
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
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("10") int limit,
            @Context final HttpServletRequest httpServletRequest) {
        UserModel sessionUser = getUserSession(httpServletRequest);
        UserModel user = getUserByUsername(username);
        if (!sessionUser.equals(user)) {
            throw new ForbiddenException();
        }

        List<SpaceModel> spaces = spaceProvider.getSpaces(user, offset, limit + 1); // +1 for check next
        int totalCount = spaceProvider.countSpaces(user);

        // Metadata
        Map<String, Object> meta = new HashMap<>();
        meta.put("totalCount", totalCount);

        // Links
        Map<String, String> links = new HashMap<>();
        links.put("first", uriInfo.getBaseUriBuilder()
                .path(NamedSpacesService.class)
                .path(NamedSpacesService.class, "getSpacesByUser")
                .build(username).toString() +
                "?offset=0" +
                "&limit=" + limit);

        links.put("last", uriInfo.getBaseUriBuilder()
                .path(NamedSpacesService.class)
                .path(NamedSpacesService.class, "getSpacesByUser")
                .build(username).toString() +
                "?offset=" + (totalCount > 0 ? (((totalCount - 1) % limit) * limit) : 0) +
                "&limit=" + limit);

        if (spaces.size() > limit) {
            links.put("next", uriInfo.getBaseUriBuilder()
                    .path(NamedSpacesService.class)
                    .path(NamedSpacesService.class, "getSpacesByUser")
                    .build(username).toString() +
                    "?offset=" + (offset + limit) +
                    "&limit=" + limit);

            spaces.remove(links.size() - 1); // Remove last item because whe added one element at the beginning
        }

        return new GenericDataRepresentation<>(spaces.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, false))
                .collect(Collectors.toList()), links, meta);
    }

    @GET
    @Path("{username}/{spaceAssignedId}")
    @Produces(MediaType.APPLICATION_JSON)
    public SpaceRepresentation getSpaceByUser(
            @PathParam("username") String username,
            @PathParam("spaceAssignedId") String spaceAssignedId,
            @Context final HttpServletRequest httpServletRequest) {
        UserModel sessionUser = getUserSession(httpServletRequest);
        UserModel user = getUserByUsername(username);
        if (!sessionUser.equals(user)) {
            throw new ForbiddenException();
        }

        SpaceModel space = getSpaceByAssignedId(spaceAssignedId);

        // Check if user has access to space
        if (user.getAllPermitedSpaces().contains(space)) {
            return modelToRepresentation.toRepresentation(space, uriInfo, false).toSpaceRepresentation();
        } else {
            throw new ForbiddenException();
        }
    }

}
