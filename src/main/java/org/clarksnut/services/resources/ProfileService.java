package org.clarksnut.services.resources;

import io.swagger.annotations.*;
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
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Map;
import java.util.Set;

@Stateless
@Path("/api/profile")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Profile", consumes = "application/json")
public class ProfileService extends AbstractResource {

    private static final Logger logger = Logger.getLogger(ProfileService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private UserProvider userProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return User Profile", notes = "This will return the profile associated to the current token. [user] role required")
    public UserRepresentation getCurrentUser(@Context final HttpServletRequest httpServletRequest) {
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
        String username = accessToken.getPreferredUsername();

        UserModel user = this.userProvider.getUserByUsername(username);
        if (user == null) {
            user = this.userProvider.addUser(username, "kc");
            logger.info("New User added");
        }
        mergeUserInfo(user, accessToken);
        return modelToRepresentation.toRepresentation(user, uriInfo, true).toUserRepresentation();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update User Profile", notes = "This will update the profile associated to the current token. [user] role required")
    public UserRepresentation currentUser(
            @Context final HttpServletRequest httpServletRequest,
            final UserRepresentation userRepresentation) {
        UserModel sessionUser = getUserSession(httpServletRequest);
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