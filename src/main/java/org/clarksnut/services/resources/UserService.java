package org.clarksnut.services.resources;

import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.representations.idm.UserRepresentation;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

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
public class UserService extends AbstractResource {

    private static final Logger logger = Logger.getLogger(UserService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private UserProvider userProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserRepresentation getCurrentUser(@Context final HttpServletRequest httpServletRequest) {
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
        String kcUsername = accessToken.getPreferredUsername();

        // Get user from DB
        UserModel user = this.userProvider.getUserByUsername(kcUsername);
        if (user == null) {
            user = this.userProvider.addUser(kcUsername, "kc");
            logger.info("New User added");
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