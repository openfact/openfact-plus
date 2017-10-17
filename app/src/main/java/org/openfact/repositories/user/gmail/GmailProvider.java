package org.openfact.repositories.user.gmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.common.collect.Lists;
import net.sf.cglib.proxy.Enhancer;
import org.jboss.logging.Logger;
import org.openfact.models.BrokerType;
import org.openfact.repositories.user.*;
import org.openfact.repositories.user.utils.CredentialHandler;
import org.openfact.oauth2.OAuth2Utils;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Stateless
@MailVendorType(BrokerType.GOOGLE)
public class GmailProvider implements MailProvider {

    private static final Logger logger = Logger.getLogger(GmailProvider.class);
    private static final int BATCH_SIZE = 1000;

    private String APPLICATION_NAME;
    private HttpTransport HTTP_TRANSPORT;
    private JsonFactory JSON_FACTORY;

    @PostConstruct
    private void init() {
        APPLICATION_NAME = "Openfact Sync";
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            JSON_FACTORY = JacksonFactory.getDefaultInstance();
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("Could not initialize http transport and/or json factory");
        }
    }

    @Override
    public List<MailUblMessageModel> getUblMessages(MailRepositoryModel repository, MailQuery query) throws MailReadException {
        Gmail gmail = buildClient(repository);

        List<MailUblMessageModel> result = new ArrayList<>();
        try {
            List<Message> messages = pullMessages(gmail, repository, query);
            for (List<Message> chunk : Lists.partition(messages, BATCH_SIZE)) {
                execBatch(chunk, gmail, repository).forEach(message -> {
                    result.add(new MailUblMessageAdapter(gmail, repository, message));
                });
            }

        } catch (IOException e) {
            throw new MailReadException("Could not pull messages", e);
        }
        return result;
    }

    private Gmail buildClient(MailRepositoryModel mailRepository) {
        Credential credential = OAuth2Utils.getCredential()
                .setRefreshToken(mailRepository.getRefreshToken());

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Credential.class);
        enhancer.setCallback(new CredentialHandler("google", credential));

        Credential proxy = (Credential) enhancer.create(
                new Class[]{Credential.AccessMethod.class},
                new Credential.AccessMethod[]{credential.getMethod()});

        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, proxy)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private List<Message> pullMessages(Gmail gmail, MailRepositoryModel repository, MailQuery query) throws IOException {
        List<Message> messages = new ArrayList<>();
        GmailQueryParser parser = new GmailQueryParser();
        String gmailQuery = parser.parse(query);

        ListMessagesResponse response = gmail.users()
                .messages()
                .list(repository.getEmail())
                .setQ(gmailQuery)
                .execute();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = gmail.users()
                        .messages()
                        .list(repository.getEmail())
                        .setQ(gmailQuery)
                        .setPageToken(pageToken)
                        .execute();
            } else {
                break;
            }
        }

        return messages;
    }

    private List<Message> execBatch(List<Message> messages, Gmail gmail, MailRepositoryModel repository) throws IOException {
        List<Message> result = new ArrayList<>();
        BatchRequest batch = gmail.batch();
        for (Message message : messages) {
            gmail.users().messages().get(repository.getEmail(), message.getId()).queue(batch, new JsonBatchCallback<Message>() {
                @Override
                public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                    logger.error("Read Message Failure code=" + e.getCode() + " message=" + e.getMessage());
                }

                @Override
                public void onSuccess(Message message, HttpHeaders responseHeaders) throws IOException {
                    result.add(message);
                }
            });
        }
        batch.execute();
        return result;
    }
}
