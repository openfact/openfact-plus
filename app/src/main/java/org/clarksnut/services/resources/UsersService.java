package org.clarksnut.services.resources;

import com.fasterxml.jackson.databind.JsonNode;
import org.clarksnut.models.QueryModel;
import org.clarksnut.models.SpaceProvider;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.utils.JacksonUtil;
import org.clarksnut.representations.idm.UserRepresentation;
import org.clarksnut.services.resources.utils.PATCH;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.AccessToken;
import org.keycloak.util.TokenUtil;
import org.clarksnut.models.UserProvider;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.clarksnut.representations.idm.UserAttributesRepresentation;
import org.clarksnut.services.util.SSOContext;

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
    private SpaceProvider spaceProvider;

    @Inject
    private UserProvider userProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    private UserModel getUserByIdentityID(String identityID) {
        UserModel user = userProvider.getUserByIdentityID(identityID);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    public UserRepresentation currentUser(@Context final HttpServletRequest httpServletRequest,
                                          final UserRepresentation userRepresentation) {

        SSOContext ssoContext = new SSOContext(httpServletRequest);
        AccessToken accessToken = ssoContext.getParsedAccessToken();

        String kcUserID = (String) accessToken.getOtherClaims().get("userID");
        UserModel user = getUserByIdentityID(kcUserID);

        UserAttributesRepresentation attributes = userRepresentation.getData().getAttributes();

        if (attributes != null) {
            // Refresh Token
            String refreshToken = attributes.getRefreshToken();
            if (refreshToken != null) {
                if (refreshToken != null) {
                    try {
                        if (TokenUtil.isOfflineToken(refreshToken)) {
                            user.setOfflineRefreshToken(refreshToken);
                        } else {
                            throw new BadRequestException("Invalid Token Type");
                        }
                    } catch (JWSInputException e) {
                        logger.error("Could not decode token", e);
                    }
                }
            }

            // Is registration completed
            Boolean registrationCompleted = attributes.getRegistrationCompleted();
            if (registrationCompleted != null) {
                user.setRegistrationCompleted(registrationCompleted);
            }

            // Context Information
            JsonNode contextInformation = attributes.getContextInformation();
            if (contextInformation != null) {
                JsonNode currentContextInformation = user.getContextInformation() != null ? user.getContextInformation() : JacksonUtil.toJsonNode("{}");
                user.setContextInformation(JacksonUtil.override(currentContextInformation, contextInformation));
            }

            // Favorite Spaces
            Set<String> favoriteSpaces = attributes.getFavoriteSpaces();
            if (favoriteSpaces != null && !favoriteSpaces.isEmpty()) {
                user.setFavoriteSpaces(favoriteSpaces);
            }

            // Profile
            if (attributes.getFullName() != null) {
                user.setFullName(attributes.getFullName());
            }
            if (attributes.getCompany() != null) {
                user.setCompany(attributes.getCompany());
            }
            if (attributes.getImageURL() != null) {
                user.setImageURL(attributes.getImageURL());
            }
            if (attributes.getUrl() != null) {
                user.setUrl(attributes.getUrl());
            }
            if (attributes.getBio() != null) {
                user.setBio(attributes.getBio());
            }

            if (attributes.getLanguage() != null) {
                user.setLanguage(attributes.getLanguage());
            }
        }

        // Build result
        return modelToRepresentation.toRepresentation(user, uriInfo).toUserRepresentation();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GenericDataRepresentation getUsers(@QueryParam("filter[username]") String usernameFilter) {
        QueryModel.Builder queryBuilder = QueryModel.builder();

        if (usernameFilter != null) {
            queryBuilder.addFilter(UserModel.USERNAME, usernameFilter);
        }

        List<UserRepresentation.Data> data = userProvider.getUsers(queryBuilder.build()).stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList());
        return new GenericDataRepresentation<>(data);
    }

    @GET
    @Path("{identityID}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserRepresentation getUser(@PathParam("identityID") String identityID) {
        UserModel user = getUserByIdentityID(identityID);
        return modelToRepresentation.toRepresentation(user, uriInfo).toUserRepresentation();
    }

}