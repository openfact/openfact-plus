package org.openfact.services.resources;

import org.openfact.models.SpaceModel;
import org.openfact.models.SpaceProvider;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.models.utils.ModelToRepresentation;
import org.openfact.representation.idm.GenericDataRepresentation;

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

    private UserModel getUserByIdentityID(String identityID) {
        UserModel user = userProvider.getUserByIdentityID(identityID);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    @GET
    @Path("{identityID}")
    @Produces(MediaType.APPLICATION_JSON)
    public GenericDataRepresentation getSpaces(
            @PathParam("identityID") String identityID,
            @QueryParam("page[offset]") Integer offset,
            @QueryParam("page[limit]") Integer limit) {
        UserModel user = getUserByIdentityID(identityID);

        if (offset == null) {
            offset = 0;
        }
        if (limit == null) {
            limit = 100;
        }

        List<SpaceModel> spaces = spaceProvider.getSpaces(user, offset, limit + 1);

        // Meta
        int totalCount = user.getOwnedSpaces().size();

        Map<String, Object> meta = new HashMap<>();
        meta.put("totalCount", totalCount);

        // Links
        Map<String, String> links = new HashMap<>();
        links.put("first", uriInfo.getBaseUriBuilder()
                .path(NamedSpacesService.class)
                .path(NamedSpacesService.class, "getSpaces")
                .build(identityID).toString() +
                "?page[offset]=0" +
                "&page[limit]=" + limit);

        links.put("last", uriInfo.getBaseUriBuilder()
                .path(NamedSpacesService.class)
                .path(NamedSpacesService.class, "getSpaces")
                .build(identityID).toString() +
                "?page[offset]=" + (totalCount > 0 ? (((totalCount - 1) % limit) * limit) : 0) +
                "&page[limit]=" + limit);

        if (spaces.size() > limit) {
            links.put("next", uriInfo.getBaseUriBuilder()
                    .path(NamedSpacesService.class)
                    .path(NamedSpacesService.class, "getSpaces")
                    .build(identityID).toString() +
                    "?page[offset]=" + (offset + limit) +
                    "&page[limit]=" + limit);

            // Remove last item
            spaces.remove(links.size() - 1);
        }

        return new GenericDataRepresentation(spaces.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList()), links, meta);
    }
}
