package org.openfact.models.broker;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class BrokerUtil {

    public boolean checkBroker(String broker) {
        Client client = ClientBuilder.newBuilder().register("").build();
        WebTarget target = client.target(getIdentityProviderTokenUrl());
        String s = target.request().get().readEntity(String.class);
        return s != null;
    }

    private String getIdentityProviderTokenUrl() {
        //return this.authServer + "/realms/" + this.realmName + "/broker/" + this.identityProvider.getAlias() + "/token";
        return null;
    }
}
