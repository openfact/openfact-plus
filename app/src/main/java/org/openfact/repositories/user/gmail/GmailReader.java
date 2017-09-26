package org.openfact.repositories.user.gmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.MethodInterceptor;
import org.openfact.OpenfactConfig;
import org.openfact.models.BrokerType;
import org.openfact.repositories.user.*;
import org.openfact.services.resources.oauth2.OAuth2Utils;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@MailVendorType(BrokerType.GOOGLE)
public class GmailReader implements MailReader {

    private String APPLICATION_NAME;
    private HttpTransport HTTP_TRANSPORT;
    private JsonFactory JSON_FACTORY;

    @PostConstruct
    private void init() {
//        APPLICATION_NAME = OpenfactConfig.getInstance().getProperty("gmail.application.name");
        APPLICATION_NAME = "Openfact Sync";
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            JSON_FACTORY = JacksonFactory.getDefaultInstance();
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("Could not initialize http transport and/or json factory");
        }
    }

    @Override
    public List<MailUBLMessage> read(MailRepository mailRepository, MailQuery mailQuery) throws MailReadException {
        Gmail gmail = buildClient(mailRepository);

        List<MailUBLMessage> result;
        try {
            result = pullMessages(gmail, mailRepository, mailQuery).stream()
                    .map(message -> new GmailUBLMessage(gmail, mailRepository, message))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new MailReadException("Could not read gmail messages", e);
        }
        return result;
    }

    private Gmail buildClient(MailRepository mailRepository) {
        Credential credential = OAuth2Utils.buildCredential().setRefreshToken(mailRepository.getRefreshToken());

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Credential.class);
        enhancer.setCallback(new CredentialHandler(credential));
        Credential proxy = (Credential) enhancer.create(new Class[]{Credential.AccessMethod.class}, new Credential.AccessMethod[]{credential.getMethod()});
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, proxy)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private List<Message> pullMessages(Gmail gmail, MailRepository mailRepository, MailQuery mailQuery) throws IOException {
        List<Message> messages = new ArrayList<>();
        String gmailQuery = new GmailQueryAdapter(mailQuery).query();

        ListMessagesResponse response = gmail.users()
                .messages()
                .list(mailRepository.getEmail())
                .setQ(gmailQuery)
                .execute();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = gmail.users()
                        .messages()
                        .list(mailRepository.getEmail())
                        .setQ(gmailQuery)
                        .setPageToken(pageToken)
                        .execute();
            } else {
                break;
            }
        }

        return messages;
    }

}
