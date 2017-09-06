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
import java.net.URI;
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

        // Links
        Map<String, String> links = new HashMap<>();
        if (spaces.size() > limit) {
            URI next = uriInfo.getBaseUriBuilder()
                    .path(NamedSpacesService.class)
                    .path(NamedSpacesService.class, "getSpaces")
                    .queryParam("page[offset]", offset + limit)
                    .queryParam("page[limit]", limit)
                    .build(identityID);
            links.put("next", next.toString());

            // Remove last item
            spaces.remove(links.size() - 1);
        }

        return new GenericDataRepresentation(spaces.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList()), links);
    }
}
