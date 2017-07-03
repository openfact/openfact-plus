package org.openfact.repository.gmail;

import com.google.api.services.gmail.model.*;
import org.openfact.models.DocumentModel;
import org.openfact.models.RepositoryDocumentProvider;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class GmailRepository implements RepositoryDocumentProvider {

    @Inject
    private GmailClient gmailClient;

    @Override
    public List<DocumentModel> getDocuments() {
        return null;
    }

    public GmailMessage getMessages(String userId, String query) throws IOException {
        // Get all messages
        ListMessagesResponse response = gmailClient.getClientService().users()
                .messages()
                .list(userId)
                .setQ(query)
                .execute();

        List<Message> messages = new ArrayList<>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = gmailClient.getClientService().users()
                        .messages()
                        .list(userId)
                        .setQ(query)
                        .setPageToken(pageToken)
                        .execute();
            } else {
                break;
            }
        }

        return new GmailMessage(messages);
    }

    public GmailMessage getMessages(String userId, BigInteger startHistoryId) throws IOException {
        ListHistoryResponse response = gmailClient.getClientService().users()
                .history()
                .list(userId)
                .setStartHistoryId(startHistoryId)
                .execute();

        List<History> histories = new ArrayList<>();
        while (response.getHistory() != null) {
            histories.addAll(response.getHistory());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = gmailClient.getClientService().users()
                        .history()
                        .list(userId)
                        .setPageToken(pageToken)
                        .setStartHistoryId(startHistoryId)
                        .execute();
            } else {
                break;
            }
        }

        List<Message> messagesAdded = new ArrayList<>();
        List<Message> messagesDeleted = new ArrayList<>();
        for (History history : histories) {
            Optional.ofNullable(history.getMessagesAdded()).ifPresent(messages -> {
                List<Message> added = messages.stream()
                        .map(HistoryMessageAdded::getMessage)
                        .collect(Collectors.toList());
                messagesAdded.addAll(added);
            });
            Optional.ofNullable(history.getMessagesDeleted()).ifPresent(messages -> {
                List<Message> deleted = messages.stream()
                        .map(HistoryMessageDeleted::getMessage)
                        .collect(Collectors.toList());
                messagesDeleted.addAll(deleted);
            });
        }

        return new GmailMessage(messagesAdded, messagesDeleted);
    }

}
