package org.openfact.services.resources;

import org.jboss.logging.Logger;
import org.keycloak.representations.AccessToken;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.utils.ModelToRepresentation;
import org.openfact.representations.idm.UserRepresentation;
import org.openfact.services.managers.KeycloakManager;
import org.openfact.services.util.SSOContext;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Map;

@Stateless
@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
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
    public UserRepresentation getCurrentUser(@Context final HttpServletRequest httpServletRequest) {
        SSOContext ssoContext = new SSOContext(httpServletRequest);
        AccessToken accessToken = ssoContext.getParsedAccessToken();

        String kcUserID = (String) accessToken.getOtherClaims().get("userID");
        String kcUsername = accessToken.getPreferredUsername();

        // Get user from DB
        UserModel user = this.userProvider.getUserByIdentityID(kcUserID);
        if (user == null) {
            user = this.userProvider.addUser(kcUserID, "kc", kcUsername);
        }
        mergeKeycloakUser(user, accessToken);

        // Result
        return modelToRepresentation.toRepresentation(user, uriInfo).toUserRepresentation();
    }

    private void mergeKeycloakUser(UserModel user, AccessToken accessToken) {
        if (accessToken.getEmail() != null && !accessToken.getEmail().equals(user.getEmail())) {
            user.setEmail(accessToken.getEmail());
        }

        if (accessToken.getName() != null && !accessToken.getName().equals(user.getFullName())) {
            user.setFullName(accessToken.getName());
        }

        Map<String, Object> attributes = accessToken.getOtherClaims();
        if (attributes != null) {
            Object bio = attributes.get("bio");
            if (bio != null) {
                String bioAttribute = (String) bio;
                if (!bioAttribute.equals(user.getBio())) user.setBio(bioAttribute);
            }

            Object company = attributes.get("company");
            if (company != null) {
                String companyAttribute = (String) company;
                if (!companyAttribute.equals(user.getCompany())) user.setCompany(companyAttribute);
            }

            Object imageURL = attributes.get("imageURL");
            if (imageURL != null) {
                String imageURLAttribute = (String) imageURL;
                if (!imageURLAttribute.equals(user.getImageURL())) user.setImageURL(imageURLAttribute);
            }

            Object url = attributes.get("url");
            if (url != null) {
                String urlAttribute = (String) url;
                if (!urlAttribute.equals(user.getUrl())) user.setUrl(urlAttribute);
            }
        }
    }

}