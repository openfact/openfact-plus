package org.clarksnut.services.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.clarksnut.models.*;
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

@Stateless
@Path("/api/request-access")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Request Access", description = "Request Access REST API", consumes = "application/json")
public class RequestsService extends AbstractResource {

    @Context
    private UriInfo uriInfo;

    @Inject
    private RequestProvider requestProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    private RequestModel getRequestById(String requestId) {
        RequestModel request = requestProvider.getRequest(requestId);
        if (request == null) {
            throw new NotFoundException();
        }
        return request;
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Request access")
    public Response requestAccessToSpace(
            @Context final HttpServletRequest httpServletRequest,
            final RequestRepresentation representation
    ) {
        RequestRepresentation.RequestData data = representation.getData();
        RequestRepresentation.RequestAttributes attributes = data.getAttributes();

        SpaceModel space = getSpaceById(attributes.getSpace());
        UserModel user = getUserById(attributes.getUser());
        PermissionType permissionType = PermissionType.valueOf(attributes.getScope().toUpperCase());
        String message = attributes.getMessage();

        if (space.getCollaborators().contains(user)) {
            return ErrorResponse.error("User is already a collaborator of Space", Response.Status.CONFLICT);
        }

        RequestModel request = requestProvider.addRequest(space, user, permissionType, message);
        RequestRepresentation.RequestData createdRequestAccessRepresentation = modelToRepresentation.toRepresentation(request);
        return Response.status(Response.Status.CREATED).entity(createdRequestAccessRepresentation.toRequestAccessSpaceToRepresentation()).build();
    }

    @PUT
    @Path("/{requestId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update request")
    public Response updateAccessSpace(
            @ApiParam(value = "Request Id") @PathParam("requestId") String requestId,
            @Context final HttpServletRequest httpServletRequest,
            final RequestRepresentation representation
    ) {
        RequestModel request = getRequestById(requestId);
        SpaceModel requestedSpace = request.getSpace();
        UserModel requestedUser = request.getUser();

        UserModel sessionUser = getUserSession(httpServletRequest);
        if (!sessionUser.getOwnedSpaces().contains(requestedSpace)) {
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
                    if (!requestedSpace.getCollaborators().contains(requestedUser)) {
                        requestedSpace.addCollaborators(request.getUser());
                    }
                    request.setStatus(RequestStatus.ACCEPTED);
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
