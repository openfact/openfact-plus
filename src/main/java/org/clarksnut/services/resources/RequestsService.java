package org.clarksnut.services.resources;

import org.clarksnut.models.*;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.RequestRepresentation;
import org.clarksnut.services.ErrorResponse;
import org.clarksnut.utils.ModelToRepresentation;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.stream.Collectors;

@Stateless
@Path("request-access")
@Consumes(MediaType.APPLICATION_JSON)
public class RequestsService {

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

    private UserModel getUserByUsername(String username) {
        UserModel user = userProvider.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestAccessToSpace(
            @Context final HttpServletRequest httpServletRequest,
            final RequestRepresentation representation) {
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
        UserModel user = getUserByUsername(accessToken.getPreferredUsername());

        RequestRepresentation.Data data = representation.getData();
        RequestRepresentation.Attributes attributes = data.getAttributes();
        SpaceModel space = getSpaceById(attributes.getSpace());

        RequestModel request = requestProvider.addRequest(space, user, PermissionType.valueOf(attributes.getScope().toUpperCase()), attributes.getMessage());
        RequestRepresentation.Data createdRequestAccessRepresentation = modelToRepresentation.toRepresentation(request);
        return Response.status(Response.Status.CREATED).entity(createdRequestAccessRepresentation.toRequestAccessSpaceToRepresentation()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GenericDataRepresentation getRequestAccess(@Context final HttpServletRequest httpServletRequest) {
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();

        UserModel user = getUserByUsername(accessToken.getPreferredUsername());

        return new GenericDataRepresentation<>(
                requestProvider.getRequests(user, RequestStatus.PENDING)
                        .stream()
                        .map(f -> modelToRepresentation.toRepresentation(f))
                        .collect(Collectors.toList())
        );
    }

    @PUT
    @Path("/{requestId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAccessSpacae(
            @PathParam("requestId") String requestId,
            final RequestRepresentation representation) {
        RequestModel request = getRequestById(requestId);

        RequestRepresentation.Data data = representation.getData();
        RequestRepresentation.Attributes attributes = data.getAttributes();

        SpaceModel space = getSpaceById(attributes.getSpace());

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

        RequestRepresentation.Data createdRequestAccessRepresentation = modelToRepresentation.toRepresentation(request);
        return Response.status(Response.Status.OK).entity(createdRequestAccessRepresentation.toRequestAccessSpaceToRepresentation()).build();
    }
}
