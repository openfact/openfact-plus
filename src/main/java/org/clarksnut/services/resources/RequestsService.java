package org.clarksnut.services.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.clarksnut.models.*;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.RequestRepresentation;
import org.clarksnut.services.ErrorResponse;
import org.clarksnut.utils.ModelToRepresentation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@Path("/api/request-access")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Request Access", consumes = "application/json")
public class RequestsService extends AbstractResource {

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

    private RequestModel getRequestById(String requestId) {
        RequestModel request = requestProvider.getRequest(requestId);
        if (request == null) {
            throw new NotFoundException();
        }
        return request;
    }

    private UserModel getUserById(String userId) {
        UserModel user = userProvider.getUser(userId);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Request access", notes = "This will request access to space. [user] role required")
    public Response requestAccessToSpace(
            @Context final HttpServletRequest httpServletRequest,
            final RequestRepresentation representation) {
        RequestRepresentation.RequestData data = representation.getData();
        RequestRepresentation.RequestAttributes attributes = data.getAttributes();

        SpaceModel space = getSpaceById(attributes.getSpace());
        UserModel user = getUserById(attributes.getUser());
        PermissionType permissionType = PermissionType.valueOf(attributes.getScope().toUpperCase());
        String message = attributes.getMessage();

        RequestModel request = requestProvider.addRequest(space, user, permissionType, message);
        RequestRepresentation.RequestData createdRequestAccessRepresentation = modelToRepresentation.toRepresentation(request);
        return Response.status(Response.Status.CREATED).entity(createdRequestAccessRepresentation.toRequestAccessSpaceToRepresentation()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Request accesses", notes = "This will return all requests on current user. [user] role required")
    public GenericDataRepresentation<List<RequestRepresentation.RequestData>> getRequestAccess(
            @ApiParam(value = "Space Ids") @QueryParam("space") List<String> spaceIds,
            @ApiParam(value = "Status", allowableValues = "pending, accepted, rejected") @DefaultValue("pending") @QueryParam("status") String status,
            @Context final HttpServletRequest httpServletRequest) {
        UserModel sessionUser = getUserSession(httpServletRequest);
        Set<SpaceModel> spaces = filterAllowedSpaces(sessionUser, spaceIds);
        RequestStatus requestStatus = RequestStatus.valueOf(status.toUpperCase());

        List<RequestRepresentation.RequestData> requests = requestProvider.getRequests(requestStatus, spaces.toArray(new SpaceModel[spaces.size()]))
                .stream()
                .map(f -> modelToRepresentation.toRepresentation(f))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(requests);
    }

    @PUT
    @Path("/{requestId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update request", notes = "This will accept or reject requests. [user] role required")
    public Response updateAccessSpace(
            @ApiParam(value = "Request Id") @PathParam("requestId") String requestId,
            @Context final HttpServletRequest httpServletRequest,
            final RequestRepresentation representation) {
        RequestModel request = getRequestById(requestId);
        SpaceModel space = request.getSpace();

        UserModel sessionUser = getUserSession(httpServletRequest);
        if (!sessionUser.getOwnedSpaces().contains(space)) {
            throw new ForbiddenException();
        }

        RequestRepresentation.RequestData data = representation.getData();
        RequestRepresentation.RequestAttributes attributes = data.getAttributes();

        if (attributes.getStatus() != null) {
            RequestStatus requestStatus = RequestStatus.valueOf(attributes.getStatus().toUpperCase());
            switch (requestStatus) {
                case PENDING:
                    return ErrorResponse.error("Could not change to this status", Response.Status.BAD_REQUEST);
                case ACCEPTED:
                    space.addCollaborators(request.getUser());
                    break;
                case REJECTED:
                    request.setStatus(RequestStatus.REJECTED);
                    break;
            }
        }

        RequestRepresentation.RequestData createdRequestAccessRepresentation = modelToRepresentation.toRepresentation(request);
        return Response.status(Response.Status.OK).entity(createdRequestAccessRepresentation.toRequestAccessSpaceToRepresentation()).build();
    }
}
