package org.openfact.services.managers;

import org.keycloak.adapters.KeycloakDeployment;
import org.openfact.broker.BrokerTokenModel;
import org.openfact.broker.BrokerType;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class BrokerManager {

    @Inject
    private KeycloakDeployment keycloakDeployment;

    /**
     * @param token
     * @return List of brokers that the token is able to read
     */
    public List<BrokerTokenModel> getAvailableBrokers(String token) {
        List<BrokerTokenModel> result = new ArrayList<>();
        for (BrokerType broker : BrokerType.values()) {
            String brokerToken = getBrokerToken(broker.getAlias(), token);
            if (brokerToken != null) {
                BrokerTokenModel brokerTokenModel = new BrokerTokenModel();
                brokerTokenModel.setToken(brokerToken);
                brokerTokenModel.setType(broker);
                brokerTokenModel.setEmail(null);

                result.add(brokerTokenModel);
            }
        }
        return result;
    }

    public String getBrokerToken(String brokerAlias, String accessToken) {
        String authServer = keycloakDeployment.getAuthServerBaseUrl();
        String realmName = keycloakDeployment.getRealm();

        ClientRequestFilter authFilter = requestContext -> {
            requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        };

        Client client = ClientBuilder.newBuilder().register(authFilter).build();
        WebTarget target = client.target(authServer + "/realms/" + realmName + "/broker/" + brokerAlias + "/token");

        Response response = target.request().get();
        if (response.getStatus() == 200) {
            return response.readEntity(String.class);
        } else {
            return null;
        }
    }

}
