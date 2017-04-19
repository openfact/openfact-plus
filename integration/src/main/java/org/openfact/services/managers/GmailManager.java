package org.openfact.services.managers;

import com.google.api.services.gmail.model.*;
import org.openfact.GmailClientService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class GmailManager {

    @Inject
    private GmailClientService gmailClientService;

    public GmailWrappedMessage getMessages(String query) throws IOException {
        // Get all messages
        ListMessagesResponse response = gmailClientService.getClientService().users()
                .messages()
                .list("me")
                .setQ(query)
                .execute();

        List<Message> messages = new ArrayList<>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = gmailClientService.getClientService().users()
                        .messages()
                        .list("me")
                        .setQ(query)
                        .setPageToken(pageToken)
                        .execute();
            } else {
                break;
            }
        }

        return new GmailWrappedMessage(messages);
    }

    public GmailWrappedMessage getMessages(BigInteger startHistoryId) throws IOException {
        ListHistoryResponse response = gmailClientService.getClientService().users()
                .history()
                .list("me")
                .setStartHistoryId(startHistoryId)
                .execute();

        List<History> histories = new ArrayList<>();
        while (response.getHistory() != null) {
            histories.addAll(response.getHistory());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = gmailClientService.getClientService().users()
                        .history()
                        .list("me")
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

        return new GmailWrappedMessage(messagesAdded, messagesDeleted);
    }

    public Optional<byte[]> getXmlFromMessage(String messageId) {
        return null;
    }

    public Optional<byte[]> getPdfFromMessage(String messageId) {
        return null;
    }

}
