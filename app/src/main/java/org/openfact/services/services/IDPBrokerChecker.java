package org.openfact.services.services;

//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
import java.util.concurrent.Callable;

public class IDPBrokerChecker implements Callable<Boolean> {

//    private final Client client;
//    private final WebTarget target;
//    private final String ssoApiUrl;
//    private final String realm;
//    private final String accessToken;
//    private final String broker;

    public IDPBrokerChecker(String ssoApiUrl, String realm, String accessToken, String broker) {
//        this.ssoApiUrl = ssoApiUrl;
//        this.realm = realm;
//        this.accessToken = accessToken;
//        this.broker = broker;
//        client = ClientBuilder.newClient();
//        target = client.target(ssoApiUrl)
//                .path("auth/realms").path(realm)
//                .path("broker").path(broker)
//                .path("token");
    }

    @Override
    public Boolean call() throws Exception {
//        Response response = target.request(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accessToken).get();
//        int status = response.getStatus();
//        response.close();
//
//        return status >= 200 && status <= 299;
        return false;
    }

}

