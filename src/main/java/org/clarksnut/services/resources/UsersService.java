package org.clarksnut.services.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.clarksnut.models.*;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.SpaceRepresentation;
import org.clarksnut.representations.idm.UserRepresentation;
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
    private UserProvider userProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    private UserModel getUserById(String id) {
        UserModel user = userProvider.getUser(id);
        if (user == null) {
            logger.error("User with id=" + id + "not found");
            throw new NotFoundException();
        }
        return user;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return List of Users")
    public GenericDataRepresentation<List<UserRepresentation.UserData>> getUsers(
            @ApiParam(value = "Username") @QueryParam("username") String username,
            @ApiParam(value = "Filter Text") @QueryParam("filterText") @DefaultValue("") String filterText,
            @ApiParam(value = "First result") @QueryParam("offset") @DefaultValue("0") int offset,
            @ApiParam(value = "Max results") @QueryParam("limit") @DefaultValue("10") int limit
    ) {
        List<UserRepresentation.UserData> data;

        if (username != null) {
            UserModel user = userProvider.getUserByUsername(username);
            if (user != null) {
                data = Collections.singletonList(modelToRepresentation.toRepresentation(user, uriInfo, false));
            } else {
                data = Collections.emptyList();
            }
        } else {
            data = userProvider.getUsers(filterText, offset, limit)
                    .stream()
                    .map(user -> modelToRepresentation.toRepresentation(user, uriInfo, false))
                    .collect(Collectors.toList());
        }

        return new GenericDataRepresentation<>(data);
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return One User")
    public UserRepresentation getUser(
            @ApiParam(value = "User Id") @PathParam("userId") String userId
    ) {
        UserModel user = getUserById(userId);
        return modelToRepresentation.toRepresentation(user, uriInfo, false).toUserRepresentation();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update User Profile")
    public UserRepresentation currentUser(
            @Context final HttpServletRequest request,
            final UserRepresentation userRepresentation
    ) {
        UserModel sessionUser = getUserSession(request);
        UserRepresentation.UserAttributesRepresentation userAttributesRepresentation = userRepresentation.getData().getAttributes();

        if (userAttributesRepresentation != null) {
            // Is registration completed
            Boolean registrationCompleted = userAttributesRepresentation.getRegistrationCompleted();
            if (registrationCompleted != null) {
                sessionUser.setRegistrationCompleted(registrationCompleted);
            }

            // Profile
            if (userAttributesRepresentation.getFullName() != null) {
                sessionUser.setFullName(userAttributesRepresentation.getFullName());
            }
            if (userAttributesRepresentation.getCompany() != null) {
                sessionUser.setCompany(userAttributesRepresentation.getCompany());
            }
            if (userAttributesRepresentation.getImageURL() != null) {
                sessionUser.setImageURL(userAttributesRepresentation.getImageURL());
            }
            if (userAttributesRepresentation.getUrl() != null) {
                sessionUser.setUrl(userAttributesRepresentation.getUrl());
            }
            if (userAttributesRepresentation.getBio() != null) {
                sessionUser.setBio(userAttributesRepresentation.getBio());
            }

            if (userAttributesRepresentation.getDefaultLanguage() != null) {
                sessionUser.setDefaultLanguage(userAttributesRepresentation.getDefaultLanguage());
            }
        }

        return modelToRepresentation.toRepresentation(sessionUser, uriInfo, true).toUserRepresentation();
    }
}