package org.openfact.services.resources.oauth2;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class OAuth2 {

    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
     * globally shared instance across your application.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** OAuth 2 scope. */
    private static final String SCOPE = "read";

    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Global instance of the JSON factory.
     */
    static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private static final String TOKEN_SERVER_URL = "https://api.dailymotion.com/oauth/token";
    private static final String AUTHORIZATION_SERVER_URL = "https://api.dailymotion.com/oauth/authorize";

    /**
     * Credential is a thread-safe OAuth 2.0 helper class for accessing protected resources using an access token.
     * When using a refresh token, Credential also refreshes the access token when the access token expires using
     * the refresh token.
     * For example, if you already have an access token, you can make a request in the following way:
     *
     * @return HttpResponse
     */
    public static HttpResponse executeGet(HttpTransport transport, JsonFactory jsonFactory, String accessToken, GenericUrl url)
            throws IOException {
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
        HttpRequestFactory requestFactory = transport.createRequestFactory(credential);

        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(new File(""));

        return requestFactory.buildGetRequest(url).execute();
    }

    /**
     * Authorizes the installed application to access user's protected data.
     */
    private static Credential authorize() throws Exception {
//        OAuth2ClientCredentials.errorIfNotSpecified();
//
//        // set up authorization code flow
//        AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
//                HTTP_TRANSPORT,
//                JSON_FACTORY,
//                new GenericUrl(TOKEN_SERVER_URL),
//                new ClientParametersAuthentication("clientId", "clientSecret"),
//                "clientId",
//                AUTHORIZATION_SERVER_URL).setScopes(Arrays.asList(SCOPE))
//                .setDataStoreFactory(DATA_STORE_FACTORY).build();
//
//        // authorize
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost(
//                OAuth2ClientCredentials.DOMAIN).setPort(OAuth2ClientCredentials.PORT).build();
//        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return null;
    }

}
