package org.openfact.services.resources;

import org.jboss.logging.Logger;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.services.managers.UserManager;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Stateless
@Path("/users")
public class UsersService {

    private static final Logger logger = Logger.getLogger(UsersService.class);

    @Inject
    private UserProvider userProvider;

    @Inject
    private UserManager userManager;

    @POST
    @Path("/{username}/repositories")
    @Produces(MediaType.APPLICATION_JSON)
    public void refreshRepositories(@PathParam("username") String username) {
        UserModel user = userProvider.getByUsername(username);

        if (user == null) {
            throw new NotFoundException();
        }

        userManager.refreshUserAvailableRepositories(user);
        userManager.syncUserRepositories(user);
    }

}