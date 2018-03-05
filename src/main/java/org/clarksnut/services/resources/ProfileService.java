package org.clarksnut.services.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clarksnut.models.UserModel;
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
import java.security.Principal;
import java.util.Map;

@Stateless
@Path("/api/profile")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Profile", description = "Profile REST API", consumes = "application/json")
public class ProfileService extends AbstractResource {

    private static final Logger logger = Logger.getLogger(ProfileService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return User Profile")
    public UserRepresentation getCurrentUser(
            @Context final HttpServletRequest request
    ) {
        Principal userPrincipal = request.getUserPrincipal();
        KeycloakPrincipal<KeycloakSecurityContext> kcPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) userPrincipal;
        AccessToken accessToken = kcPrincipal.getKeycloakSecurityContext().getToken();
        String username = accessToken.getPreferredUsername();
        String providerIdentityId = userPrincipal.getName();

        // Get user from DB
        UserModel user = this.userProvider.getUserByUsername(username);
        if (user == null) {
            user = this.userProvider.addUser(username, "kc", providerIdentityId);
        }

        mergeUserInfo(user, accessToken);
        return modelToRepresentation.toRepresentation(user, uriInfo, true).toUserRepresentation();
    }

    private void mergeUserInfo(UserModel user, AccessToken accessToken) {
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