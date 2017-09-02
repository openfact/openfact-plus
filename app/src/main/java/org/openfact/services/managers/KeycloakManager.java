package org.openfact.services.managers;

import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.representations.idm.UserRepresentation;
import org.openfact.models.ModelFetchException;
import org.openfact.services.resources.KeycloakDeploymentConfig;

import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Stateless
public class KeycloakManager {

    public UserRepresentation getUser(String userID, String accessToken) throws ModelFetchException {
        WebTarget target = getKeycloakTarget(accessToken, "users", userID);
        Response response = target.request().get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.readEntity(UserRepresentation.class);
        } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            return null;
        } else {
            throw new ModelFetchException("Could not fetch keycloak user");
        }
    }

    public WebTarget getKeycloakTarget(String accessToken, String... path) {
        KeycloakDeployment keycloakDeployment = KeycloakDeploymentConfig.getInstance().getKeycloakDeployment();

        String authServer = keycloakDeployment.getAuthServerBaseUrl();
        String realmName = keycloakDeployment.getRealm();

        ClientRequestFilter authFilter = requestContext -> {
            requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        };

        Client client = ClientBuilder.newBuilder().register(authFilter).build();
        WebTarget target = client.target(authServer + "/realms/" + realmName);

        for (String p : path) {
            target.path(p);
        }
        return target;
    }

}
