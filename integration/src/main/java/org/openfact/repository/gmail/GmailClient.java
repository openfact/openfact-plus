package org.openfact.repository.gmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import java.io.*;
import java.util.Arrays;
import java.util.List;

@Stateless
public class GmailClient {

    private static final Logger logger = Logger.getLogger(GmailClient.class);

    private static final String APPLICATION_NAME = "Openfact Sync Gmail API";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static HttpTransport HTTP_TRANSPORT;
    private static DataStoreFactory DATA_STORE_FACTORY;
    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new AbstractDataStoreFactory() {
                @Override
                protected <V extends Serializable> DataStore<V> createDataStore(String id) throws IOException {
                    return null;
                }
            };
        } catch (Throwable t) {
            logger.error("Could not init Google GMAIL API Configuration", t);
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    private Credential authorize() throws IOException {
        String hostClientSecret = System.getenv("GMAIL_CLIENT_SECRET");

        // Load client secrets.
        InputStream in;
        if (hostClientSecret != null) {
            in = new FileInputStream(new File(hostClientSecret));
        } else {
            in = this.getClass().getResourceAsStream("/META-INF/client_secret.json");
        }
        if (in == null) {
            throw new IllegalStateException("Could not find a valid google client_secret.json");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("online")
                .build();

        String REDIRECT_URI = "http://google.com";

        String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
        System.out.println("Please open the following URL in your " + "browser then type the authorization code:");
        System.out.println("  " + url);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code = br.readLine();

        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
        GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);

//        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    /**
     * Build and return an authorized Gmail client clientService.
     *
     * @return an authorized Gmail client clientService
     * @throws IOException
     */
    public Gmail getClientService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

}
