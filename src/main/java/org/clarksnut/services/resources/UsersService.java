package org.clarksnut.services.resources;

import io.swagger.annotations.*;
import org.clarksnut.models.QueryModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.UserAttributesRepresentation;
import org.clarksnut.representations.idm.UserRepresentation;
import org.clarksnut.services.resources.utils.PATCH;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Set;
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
    public GenericDataRepresentation<List<UserRepresentation.Data>> getUsers(
            @ApiParam(value = "Username") @QueryParam("username") String username) {
        QueryModel.Builder queryBuilder = QueryModel.builder();

        if (username != null) {
            queryBuilder.addFilter(UserModel.USERNAME, username);
        }

        List<UserRepresentation.Data> data = userProvider.getUsers(queryBuilder.build())
                .stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo, false))
                .collect(Collectors.toList());
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