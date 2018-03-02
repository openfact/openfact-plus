package org.clarksnut.services.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.clarksnut.models.QueryModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.UserRepresentation;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Users", consumes = "application/json")
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
    @ApiOperation(value = "Get Users", notes = "This will search users. [view-users] role required")
    public GenericDataRepresentation<List<UserRepresentation.UserData>> getUsers(
            @ApiParam(value = "Username") @QueryParam("username") String username,
            @ApiParam(value = "Filter Text") @QueryParam("filterText") @DefaultValue("") String filterText) {
        List<UserRepresentation.UserData> data;
        if (username != null) {
            UserModel user = userProvider.getUserByUsername(username);
            if (user != null) {
                data = Collections.singletonList(modelToRepresentation.toRepresentation(user, uriInfo, false));
            } else {
                data = Collections.emptyList();
            }
        } else {
            data = userProvider.getUsers(filterText).stream()
                    .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, false))
                    .collect(Collectors.toList());
        }

        return new GenericDataRepresentation<>(data);
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get User", notes = "This will return the requested user. [view-users] role required")
    public UserRepresentation getUser(
            @ApiParam(value = "User Id") @PathParam("userId") String userId) {
        UserModel user = getUserById(userId);
        return modelToRepresentation.toRepresentation(user, uriInfo, false).toUserRepresentation();
    }

}