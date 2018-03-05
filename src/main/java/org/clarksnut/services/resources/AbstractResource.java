package org.clarksnut.services.resources;

import org.clarksnut.models.*;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractResource {

    @Inject
    protected UserProvider userProvider;

    @Inject
    protected SpaceProvider spaceProvider;

    @Inject
    protected DocumentProvider documentProvider;

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
        Set<String> permittedSpaceAssignedIds = user.getAllPermittedSpaces().stream().map(SpaceModel::getAssignedId).collect(Collectors.toSet());
        return permittedSpaceAssignedIds.contains(document.getSupplierAssignedId()) ||
                permittedSpaceAssignedIds.contains(document.getCustomerAssignedId());
    }

    protected Set<SpaceModel> filterAllowedSpaces(UserModel user, Collection<String> spaceIds) {
        if (spaceIds.isEmpty()) {
            return new HashSet<>(user.getAllPermittedSpaces());
        } else {
            return user.getAllPermittedSpaces().stream()
                    .filter(p -> spaceIds.contains(p.getAssignedId()))
                    .collect(Collectors.toSet());
        }
    }

    protected UserModel getUserById(String id) {
        UserModel user = userProvider.getUser(id);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    protected SpaceModel getSpaceById(String spaceId) {
        SpaceModel space = spaceProvider.getSpace(spaceId);
        if (space == null) {
            throw new NotFoundException();
        }
        return space;
    }

    protected DocumentModel getDocumentById(String documentId) {
        DocumentModel document = documentProvider.getDocument(documentId);
        if (document == null) {
            throw new NotFoundException();
        }
        return document;
    }

}
