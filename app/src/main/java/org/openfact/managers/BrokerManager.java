package org.openfact.managers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.codehaus.jackson.map.ObjectMapper;
import org.keycloak.adapters.KeycloakDeployment;
import org.openfact.models.BrokerType;
import org.openfact.representations.idm.TokenRepresentation;
import org.openfact.services.resources.KeycloakDeploymentConfig;
import org.openfact.oauth2.OAuth2Utils;

import javax.ejb.Stateless;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class BrokerManager {

    /**
     * @param refreshToken refresh token
     * @return List of brokers that the token is able to read.
     * e.g [{google, email.google.com}, {microsoft, email.outlook.com}]
     */
    public Map<String, BrokerType> getLinkedBrokers(String refreshToken) throws IOException {
        Map<String, BrokerType> result = new HashMap<>();
        for (BrokerType broker : BrokerType.values()) {
            TokenRepresentation token = getBrokerToken(broker.getAlias(), refreshToken);
            if (token != null) {
                DecodedJWT jwt = JWT.decode(token.getId_token());
                result.put(jwt.getClaim("email").asString(), broker);
            }
        }
        return result;
    }

    private TokenRepresentation getBrokerToken(String broker, String refreshToken) throws IOException {
        KeycloakDeployment keycloakDeployment = KeycloakDeploymentConfig.getInstance().getDeployment();
        String authServer = keycloakDeployment.getAuthServerBaseUrl();
        String realmName = keycloakDeployment.getRealm();

        Credential credential = OAuth2Utils.getCredential().setRefreshToken(refreshToken);
        HttpTransport transport = new NetHttpTransport();
        HttpRequestFactory requestFactory = transport.createRequestFactory(credential);

        GenericUrl url = new GenericUrl(authServer + "/realms/" + realmName + "/broker/" + broker + "/token");
        HttpResponse execute = requestFactory.buildGetRequest(url).execute();

        TokenRepresentation result = null;
        if (execute.isSuccessStatusCode()) {
            String response = execute.parseAsString();
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response, TokenRepresentation.class);
        }
        execute.disconnect();
        return result;
    }

}
