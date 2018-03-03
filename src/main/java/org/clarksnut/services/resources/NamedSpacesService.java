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
import org.clarksnut.utils.ModelToRepresentation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@Path("/api/namedspaces")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Named Spaces", description = "NamedSpaces REST API", consumes = "application/json")
public class NamedSpacesService extends AbstractResource {

    @Context
    private UriInfo uriInfo;

    @Inject
    private UserProvider userProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    private UserModel getUserById(String userId) {
        UserModel user = userProvider.getUser(userId);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    @GET
    @Path("/{userId}/spaces")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return allowed Spaces of User")
    public GenericDataRepresentation<List<SpaceRepresentation.SpaceData>> getUserSpaces(
            @ApiParam(value = "User Id", allowableValues = "me, userId") @PathParam("userId") String userId,
            @ApiParam(value = "Role", allowableValues = "owner, collaborator") @QueryParam("role") @DefaultValue("owner") String role,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max results") @QueryParam("limit") @DefaultValue("10") int limit
    ) throws ErrorResponseException {
        UserModel user = getUserById(userId);


        int totalCount;
        List<SpaceModel> spaces;

        // Search
        PermissionType permissionType = PermissionType.valueOf(role.toUpperCase());
        switch (permissionType) {
            case OWNER:
                spaces = user.getOwnedSpaces(offset, limit + 1);
                totalCount = user.getOwnedSpaces().size();
                break;
            case COLLABORATOR:
                spaces = user.getCollaboratedSpaces(offset, limit + 1);
                totalCount = user.getCollaboratedSpaces().size();
                break;
            default:
                throw new ErrorResponseException("Invalid Role", Response.Status.BAD_REQUEST);
        }

        // Metadata
        Map<String, Object> meta = new HashMap<>();
        meta.put("totalCount", totalCount);

        // Links
        Map<String, String> links = new HashMap<>();
        links.put("first", uriInfo.getBaseUriBuilder()
                .path(NamedSpacesService.class)
                .path(NamedSpacesService.class, "getUserSpaces")
                .build(userId).toString() +
                "?role=" + role +
                "?offset=0" +
                "&limit=" + limit);

        links.put("last", uriInfo.getBaseUriBuilder()
                .path(NamedSpacesService.class)
                .path(NamedSpacesService.class, "getUserSpaces")
                .build(userId).toString() +
                "?role=" + role +
                "?offset=" + (totalCount > 0 ? (((totalCount - 1) % limit) * limit) : 0) +
                "&limit=" + limit);

        if (spaces.size() > limit) {
            links.put("next", uriInfo.getBaseUriBuilder()
                    .path(NamedSpacesService.class)
                    .path(NamedSpacesService.class, "getUserSpaces")
                    .build(userId).toString() +
                    "?role=" + role +
                    "?offset=" + (offset + limit) +
                    "&limit=" + limit);

            // Remove last item
            spaces.remove(spaces.size() - 1);
        }


        List<SpaceRepresentation.SpaceData> spacesData = spaces.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, true))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(spacesData, links, meta);
    }

}
