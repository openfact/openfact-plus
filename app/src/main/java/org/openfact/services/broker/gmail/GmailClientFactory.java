package org.openfact.services.broker.gmail;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import org.openfact.models.Constants;
import org.openfact.models.UserRepositoryModel;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Stateless
public class GmailClientFactory {

//    private static HttpTransport HTTP_TRANSPORT;
//    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//
//    @PostConstruct
//    private void init() throws GeneralSecurityException, IOException {
//        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//    }
//
//    public Gmail getClientService(UserRepositoryModel repository) {
//        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, null)
//                .setApplicationName(Constants.GMAIL_APPLICATION_NAME).build();
//    }

}
