package org.openfact;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Singleton
@Startup
public class OpenfactStartUp {

    private static final Logger logger = Logger.getLogger(OpenfactStartUp.class);

    private static final String APPLICATION_NAME = "OpenfactSyn Gmail API";
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/gmail-openfact");
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport HTTP_TRANSPORT;
    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);

    private Gmail service;
    private final static String user = "me";

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            logger.error("Could not init Google GMAIL API Configuration", t);
            System.exit(1);
        }
    }

    @PostConstruct
    private void init() {
        try {
            service = getGmailService();
        } catch (IOException e) {
            logger.error("Cloud not start Google GMAIL API Service", e);
            System.exit(1);
        }

        try {
            JsonBatchCallback<Message> callback = new JsonBatchCallback<Message>() {
                @Override
                public void onSuccess(Message message, HttpHeaders responseHeaders) throws IOException {
                    List<MessagePart> parts = message.getPayload().getParts();
                    for (MessagePart part : parts) {
                        if (part.getFilename() != null && part.getFilename().length() > 0) {
                            String fileName = part.getFilename();
                            String attachmentId = part.getBody().getAttachmentId();
                            MessagePartBody messagePartBody = service.users().messages().attachments().get(user, message.getId(), attachmentId).execute();

                            Base64 base64Url = new Base64(true);
                            byte[] fileByteArray = base64Url.decode(messagePartBody.getData());
                        }
                    }
                }

                @Override
                public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                }
            };
            BatchRequest batch = service.batch();


            // Get all messages
            ListMessagesResponse response = service.users().messages().list(user).execute();
            List<Message> messages = new ArrayList<>();
            while (response.getMessages() != null) {
                messages.addAll(response.getMessages());
                if (response.getNextPageToken() != null) {
                    String pageToken = response.getNextPageToken();
                    response = service.users().messages().list(user).setPageToken(pageToken).execute();
                } else {
                    break;
                }
            }

            messages.forEach(message -> {
                try {
                    service.users().messages().get(user, message.getId()).queue(batch, callback);
                } catch (IOException e) {
                    throw new IllegalStateException("could not read email");
                }
            });

            batch.execute();

            ForkJoinPool pool = new ForkJoinPool();
            EmailReader task = new EmailReader(100);
            BigDecimal historyId = pool.invoke(task);

        } catch (IOException e) {
            throw new IllegalStateException("Could not read credentials");
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    private Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = OpenfactStartUp.class.getResourceAsStream("/META-INF/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     *
     * @return an authorized Gmail client service
     * @throws IOException
     */
    private Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

}
