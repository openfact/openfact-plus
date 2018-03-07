package org.clarksnut.services.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.clarksnut.files.FileModel;
import org.clarksnut.models.*;
import org.clarksnut.query.RangeQuery;
import org.clarksnut.query.TermQuery;
import org.clarksnut.query.TermsQuery;
import org.clarksnut.report.ExportFormat;
import org.clarksnut.report.ReportTemplateConfiguration;
import org.clarksnut.report.ReportTemplateProvider;
import org.clarksnut.report.exceptions.ReportException;
import org.clarksnut.representations.idm.*;
import org.clarksnut.services.ErrorResponse;
import org.clarksnut.services.ErrorResponseException;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;

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
@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Users", description = "Users REST API", consumes = "application/json")
public class UsersService extends AbstractResource {

    private static final Logger logger = Logger.getLogger(UsersService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private RequestProvider requestProvider;

    @Inject
    private PartyProvider partyProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Users")
    public GenericDataRepresentation<List<UserRepresentation.UserData>> getUsers(
            @ApiParam(value = "Username") @QueryParam("username") String username,
            @ApiParam(value = "Filter Text") @QueryParam("filterText") String filterText,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max results") @QueryParam("limit") @DefaultValue("10") int limit
    ) {
        List<UserRepresentation.UserData> data;

        if (username != null) {
            UserModel user = userProvider.getUserByUsername(username);
            if (user == null) {
                data = Collections.emptyList();
            } else {
                data = Collections.singletonList(modelToRepresentation.toRepresentation(user, uriInfo, false));
            }
        } else {
            Optional<String> filterTextOptional = Optional.ofNullable(filterText);
            data = userProvider.getUsers(filterTextOptional.orElse(""), offset, limit)
                    .stream()
                    .map(user -> modelToRepresentation.toRepresentation(user, uriInfo, false))
                    .collect(Collectors.toList());
        }

        return new GenericDataRepresentation<>(data);
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get User")
    public UserRepresentation getUser(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @Context final HttpServletRequest request
    ) {
        UserModel user;
        boolean fullInfo = false;

        if (userId.equals("me")) {
            user = getUserSession(request);
            fullInfo = true;
        } else {
            user = getUserById(userId);
        }

        return modelToRepresentation.toRepresentation(user, uriInfo, fullInfo).toUserRepresentation();
    }

    @PUT
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update User")
    public UserRepresentation updateUser(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @Context final HttpServletRequest request,
            final UserRepresentation userRepresentation
    ) throws ErrorResponseException {
        UserModel user;

        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        UserRepresentation.UserAttributesRepresentation attributes = userRepresentation.getData().getAttributes();

        if (attributes != null) {
            // Is registration completed
            Boolean registrationCompleted = attributes.getRegistrationCompleted();
            if (registrationCompleted != null) {
                user.setRegistrationCompleted(registrationCompleted);
            }

            // Profile
            if (attributes.getFullName() != null) {
                user.setFullName(attributes.getFullName());
            }
            if (attributes.getCompany() != null) {
                user.setCompany(attributes.getCompany());
            }
            if (attributes.getImageURL() != null) {
                user.setImageURL(attributes.getImageURL());
            }
            if (attributes.getUrl() != null) {
                user.setUrl(attributes.getUrl());
            }
            if (attributes.getBio() != null) {
                user.setBio(attributes.getBio());
            }

            if (attributes.getDefaultLanguage() != null) {
                user.setDefaultLanguage(attributes.getDefaultLanguage());
            }
        }

        return modelToRepresentation.toRepresentation(user, uriInfo, true).toUserRepresentation();
    }

    /*
     * Parties
     * */

    @GET
    @Path("/{userId}/parties")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get parties")
    public GenericDataRepresentation<List<PartyRepresentation.PartyData>> getParties(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Filter Text") @QueryParam("filterText") String filterText,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max result") @QueryParam("limit") @DefaultValue("10") int limit,
            @ApiParam(value = "Space Ids") @QueryParam("space") List<String> spaceIds,
            @Context HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;
        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        Set<SpaceModel> spaces = filterAllowedSpaces(user, spaceIds);

        Optional<String> optionalFilterText = Optional.ofNullable(filterText);
        List<PartyRepresentation.PartyData> parties = partyProvider.getParties(optionalFilterText.orElse(""), limit, spaces.toArray(new SpaceModel[spaces.size()]))
                .stream()
                .map(party -> modelToRepresentation.toRepresentation(party))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(parties);
    }

    /*
     * Notifications
     * */

    @GET
    @Path("/{userId}/notifications")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Notifications")
    public NotificationsRepresentation getNotifications(
            @ApiParam(value = "User Id") @PathParam("userId") String userId,
            @ApiParam(value = "Space Ids") @QueryParam("space") List<String> spaceIds,
            @ApiParam(value = "Status", allowableValues = "pending, accepted, rejected") @DefaultValue("pending") @QueryParam("status") String status,
            @Context final HttpServletRequest request
    ) throws ErrorResponseException {
        UserModel user;
        if (userId.equals("me")) {
            user = getUserSession(request);
        } else {
            throw new ErrorResponseException("Error", Response.Status.NOT_IMPLEMENTED);
        }

        Set<SpaceModel> spaces = filterAllowedSpaces(user, spaceIds);
        RequestStatus requestStatus = RequestStatus.valueOf(status.toUpperCase());

        List<RequestRepresentation.RequestData> requests = requestProvider.getRequests(requestStatus, spaces.toArray(new SpaceModel[spaces.size()]))
                .stream()
                .map(f -> modelToRepresentation.toRepresentation(f))
                .collect(Collectors.toList());

        // Representation
        NotificationsRepresentation.NotificationsData data = new NotificationsRepresentation.NotificationsData();
        data.setType(ModelType.NOTIFICATIONS.getAlias());

        NotificationsRepresentation.NotificationsAttributes attributes = new NotificationsRepresentation.NotificationsAttributes();
        data.setAttributes(attributes);

        attributes.setRequestAccesses(requests);
        return data.toNotificationsRepresentation();
    }
}