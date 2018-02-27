package org.clarksnut.services.resources;

import org.clarksnut.models.DocumentModel;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.models.exceptions.ModelForbiddenException;
import org.clarksnut.representations.idm.GenericDataRepresentation;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractResource {

    @Inject
    protected UserProvider userProvider;

    protected UserModel getUserSession(HttpServletRequest httpServletRequest) {
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
        String username = accessToken.getPreferredUsername();

        UserModel user = userProvider.getUserByUsername(username);
        if (user == null) {
            throw new ForbiddenException();
        }
        return user;
    }

    protected boolean isUserAllowedToViewDocument(UserModel user, DocumentModel document) {
        Set<String> permittedSpaceAssignedIds = user.getAllPermitedSpaces().stream().map(SpaceModel::getAssignedId).collect(Collectors.toSet());
        return permittedSpaceAssignedIds.contains(document.getSupplierAssignedId()) ||
                permittedSpaceAssignedIds.contains(document.getCustomerAssignedId());
    }

    protected Set<SpaceModel> filterAllowedSpaces(UserModel user, Collection<String> spaceIds) {
        return user.getAllPermitedSpaces().stream()
                .filter(p -> spaceIds.contains(p.getAssignedId()))
                .collect(Collectors.toSet());
    }
}
