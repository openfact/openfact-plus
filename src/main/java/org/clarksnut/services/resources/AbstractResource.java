package org.clarksnut.services.resources;

import org.clarksnut.models.DocumentModel;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.ClientAuthorizationContext;
import org.keycloak.authorization.client.representation.RegistrationResponse;
import org.keycloak.authorization.client.representation.ResourceRepresentation;
import org.keycloak.authorization.client.representation.ScopeRepresentation;
import org.keycloak.authorization.client.resource.ProtectionResource;
import org.keycloak.representations.AccessToken;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractResource {

    public static final String SCOPE_USER_VIEW = "user:view";
    public static final String SCOPE_USER_MANAGE = "user:manage";

    public static final String SCOPE_SPACE_VIEW = "space:view";
    public static final String SCOPE_SPACE_DELETE = "space:delete";

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
        Set<String> permittedSpaceAssignedIds = user.getAllPermittedSpaces().stream().map(SpaceModel::getAssignedId).collect(Collectors.toSet());
        return permittedSpaceAssignedIds.contains(document.getSupplierAssignedId()) ||
                permittedSpaceAssignedIds.contains(document.getCustomerAssignedId());
    }

    protected Set<SpaceModel> filterAllowedSpaces(UserModel user, Collection<String> spaceIds) {
        return user.getAllPermittedSpaces().stream()
                .filter(p -> spaceIds.contains(p.getAssignedId()))
                .collect(Collectors.toSet());
    }

    protected void createUserProtectedResource(UserModel user, HttpServletRequest request) {
        try {
            Set<ScopeRepresentation> scopes = new HashSet<>();

            scopes.add(new ScopeRepresentation(SCOPE_USER_VIEW));
            scopes.add(new ScopeRepresentation(SCOPE_USER_MANAGE));

            ResourceRepresentation spaceResource = new ResourceRepresentation(user.getUsername(), scopes, "/users/" + user.getId(), "http://clarksnut.com/user");

            spaceResource.setOwner(user.getIdentityId());

            RegistrationResponse response = getAuthzClient(request).protection().resource().create(spaceResource);

            user.setExternalId(response.getId());
        } catch (Exception e) {
            throw new RuntimeException("Could not register user protected resource.", e);
        }
    }

    protected void createSpaceProtectedResource(SpaceModel space, UserModel owner, HttpServletRequest request) {
        try {
            Set<ScopeRepresentation> scopes = new HashSet<>();

            scopes.add(new ScopeRepresentation(SCOPE_SPACE_VIEW));
            scopes.add(new ScopeRepresentation(SCOPE_SPACE_DELETE));

            ResourceRepresentation spaceResource = new ResourceRepresentation(space.getId(), scopes, "/space/" + space.getId(), "http://clarksnut.com/space");

            spaceResource.setOwner(owner.getId());

            RegistrationResponse response = getAuthzClient(request).protection().resource().create(spaceResource);

            space.setExternalId(response.getId());
        } catch (Exception e) {
            throw new RuntimeException("Could not register protected resource.", e);
        }
    }

    protected void deleteUserProtectedResource(UserModel user, HttpServletRequest request) {
        String uri = "/users/" + user.getId();
        deleteProtectedResource(uri, request);
    }

    protected void deleteProtectedResource(SpaceModel space, HttpServletRequest request) {
        String uri = "/space/" + space.getId();
        deleteProtectedResource(uri, request);
    }

    protected void deleteProtectedResource(String uri, HttpServletRequest request) {
        try {
            ProtectionResource protection = getAuthzClient(request).protection();
            Set<String> search = protection.resource().findByFilter("uri=" + uri);

            if (search.isEmpty()) {
                throw new RuntimeException("Could not find protected resource with URI [" + uri + "]");
            }

            protection.resource().delete(search.iterator().next());
        } catch (Exception e) {
            throw new RuntimeException("Could not search protected resource.", e);
        }
    }

    protected AuthzClient getAuthzClient(HttpServletRequest request) {
        return getAuthorizationContext(request).getClient();
    }

    private ClientAuthorizationContext getAuthorizationContext(HttpServletRequest request) {
        return ClientAuthorizationContext.class.cast(getKeycloakSecurityContext(request).getAuthorizationContext());
    }

    private KeycloakSecurityContext getKeycloakSecurityContext(HttpServletRequest request) {
        return KeycloakSecurityContext.class.cast(request.getAttribute(KeycloakSecurityContext.class.getName()));
    }
}
