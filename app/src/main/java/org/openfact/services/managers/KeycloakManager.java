package org.openfact.services.managers;

import javax.ejb.Stateless;

@Stateless
public class KeycloakManager {

//    public UserRepresentation getUser(String userID, String accessToken) throws FileFetchException {
//        WebTarget target = getKeycloakTarget(accessToken, "users", userID);
//        Response response = target.request().get();
//
//        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
//            return response.readEntity(UserRepresentation.class);
//        } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
//            return null;
//        } else {
//            throw new FileFetchException("Could not fetch keycloak user");
//        }
//    }
//
//    public WebTarget getKeycloakTarget(String accessToken, String... path) {
//        KeycloakDeployment keycloakDeployment = KeycloakDeploymentConfig.getInstance().getDeployment();
//
//        String authServer = keycloakDeployment.getAuthServerBaseUrl();
//        String realmName = keycloakDeployment.getRealm();
//
//        ClientRequestFilter authFilter = requestContext -> {
//            requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
//        };
//
//        Client client = ClientBuilder.newBuilder().register(authFilter).build();
//        WebTarget target = client.target(authServer + "/realms/" + realmName);
//
//        for (String p : path) {
//            target.path(p);
//        }
//        return target;
//    }

}
