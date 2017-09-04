package org.openfact.services.resources;

import org.jboss.logging.Logger;
import org.keycloak.representations.AccessToken;
import org.openfact.models.ModelFetchException;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.models.utils.ModelToRepresentation;
import org.openfact.representation.idm.DataRepresentation;
import org.openfact.representation.idm.UserRepresentation;
import org.openfact.services.managers.KeycloakManager;
import org.openfact.services.util.SSOContext;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

@Stateless
@Path("/user")
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpServletRequest request;

    @Inject
    private UserProvider userProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @Inject
    private KeycloakManager keycloakManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public DataRepresentation getCurrentUser(@Context final HttpServletRequest httpServletRequest) {
        SSOContext ssoContext = new SSOContext(httpServletRequest);
        AccessToken accessToken = ssoContext.getParsedAccessToken();
        String kcUserID = accessToken.getId();

        // Fetch Keycloak user
        org.keycloak.representations.idm.UserRepresentation kcUser;
        try {
            kcUser = keycloakManager.getUser(kcUserID, ssoContext.getAccessToken());
        } catch (ModelFetchException e) {
            throw new NotFoundException("Could not fetch user from Keycloak");
        }

        // Null means that user no longer exists on Keycloak
        if (kcUser == null) {
            logger.error("User " + kcUserID + "not exists on Keycloak");
            throw new NotFoundException("User not exists on Keycloak");
        }

        // Get user from DB
        UserModel user = this.userProvider.getUser(kcUserID);
        if (user == null) {
            user = this.userProvider.addUser(kcUserID, "kc");
            mergeKeycloakUser(user, kcUser);
        }

        // Result
        return new DataRepresentation(modelToRepresentation.toRepresentation(user, uriInfo));
    }

    private void mergeKeycloakUser(UserModel user, org.keycloak.representations.idm.UserRepresentation kcUser) {
        user.setUsername(kcUser.getUsername());
        user.setEmail(kcUser.getEmail());
        user.setFullName(kcUser.getFirstName() + " " + kcUser.getLastName());

        Map<String, List<String>> attributes = kcUser.getAttributes();
        if (attributes != null) {
            List<String> bio = attributes.get("bio");
            if (bio != null && !bio.isEmpty()) {
                user.setBio(bio.get(0));
            }

            List<String> company = attributes.get("company");
            if (company != null && !company.isEmpty()) {
                user.setCompany(company.get(0));
            }

            List<String> imageURL = attributes.get("imageURL");
            if (imageURL != null && !imageURL.isEmpty()) {
                user.setImageURL(imageURL.get(0));
            }

            List<String> url = attributes.get("url");
            if (url != null && !url.isEmpty()) {
                user.setUrl(url.get(0));
            }
        }
    }

}