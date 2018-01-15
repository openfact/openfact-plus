package org.clarksnut.services.resources;

import com.fasterxml.jackson.databind.JsonNode;
import org.clarksnut.models.QueryModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.models.utils.JacksonUtil;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.UserAttributesRepresentation;
import org.clarksnut.representations.idm.UserRepresentation;
import org.clarksnut.services.resources.utils.PATCH;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;
import org.keycloak.KeycloakPrincipal;

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
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
public class UsersService {

    private static final Logger logger = Logger.getLogger(UsersService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private UserProvider userProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    private UserModel getUser(HttpServletRequest httpServletRequest) {
        KeycloakPrincipal principal = (KeycloakPrincipal) httpServletRequest.getUserPrincipal();
        String username = principal.getKeycloakSecurityContext().getIdToken().getPreferredUsername();

        UserModel user = userProvider.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    private UserModel getUserByIdentityID(String identityID) {
        UserModel user = userProvider.getUserByIdentityID(identityID);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    @GET
    @Path("{identityID}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserRepresentation getUser(@PathParam("identityID") String identityID) {
        UserModel user = getUserByIdentityID(identityID);
        return modelToRepresentation.toRepresentation(user, uriInfo).toUserRepresentation();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GenericDataRepresentation getUsers(@QueryParam("filter[username]") String username) {
        QueryModel.Builder queryBuilder = QueryModel.builder();

        if (username != null) {
            queryBuilder.addFilter(UserModel.USERNAME, username);
        }

        List<UserRepresentation.Data> data = userProvider.getUsers(queryBuilder.build())
                .stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(data);
    }

    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    public UserRepresentation currentUser(@Context final HttpServletRequest httpServletRequest, final UserRepresentation userRepresentation) {
        UserModel user = getUser(httpServletRequest);
        UserAttributesRepresentation userAttributesRepresentation = userRepresentation.getData().getAttributes();

        if (userAttributesRepresentation != null) {
            // Is registration completed
            Boolean registrationCompleted = userAttributesRepresentation.getRegistrationCompleted();
            if (registrationCompleted != null) {
                user.setRegistrationCompleted(registrationCompleted);
            }

            // Context Information
            JsonNode contextInformation = userAttributesRepresentation.getContextInformation();
            if (contextInformation != null) {
                JsonNode currentContextInformation = user.getContextInformation() != null ? user.getContextInformation() : JacksonUtil.toJsonNode("{}");
                user.setContextInformation(JacksonUtil.override(currentContextInformation, contextInformation));
            }

            // Favorite Spaces
            Set<String> favoriteSpaces = userAttributesRepresentation.getFavoriteSpaces();
            if (favoriteSpaces != null && !favoriteSpaces.isEmpty()) {
                user.setFavoriteSpaces(favoriteSpaces);
            }

            // Profile
            if (userAttributesRepresentation.getFullName() != null) {
                user.setFullName(userAttributesRepresentation.getFullName());
            }
            if (userAttributesRepresentation.getCompany() != null) {
                user.setCompany(userAttributesRepresentation.getCompany());
            }
            if (userAttributesRepresentation.getImageURL() != null) {
                user.setImageURL(userAttributesRepresentation.getImageURL());
            }
            if (userAttributesRepresentation.getUrl() != null) {
                user.setUrl(userAttributesRepresentation.getUrl());
            }
            if (userAttributesRepresentation.getBio() != null) {
                user.setBio(userAttributesRepresentation.getBio());
            }

            if (userAttributesRepresentation.getLanguage() != null) {
                user.setLanguage(userAttributesRepresentation.getLanguage());
            }
        }

        return modelToRepresentation.toRepresentation(user, uriInfo).toUserRepresentation();
    }

}