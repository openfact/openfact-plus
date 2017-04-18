package org.openfact;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GmailSync {

    private Gmail gmail;

    public GmailSync(Gmail gmail) {
        this.gmail = gmail;
    }

    public WrapperMessage getMessages() throws IOException {
        // Get all messages
        ListMessagesResponse response = gmail.users()
                .messages()
                .list("me")
                .execute();

        List<Message> messages = new ArrayList<>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = gmail.users()
                        .messages()
                        .list("me")
                        .setPageToken(pageToken)
                        .execute();
            } else {
                break;
            }
        }

        return new WrapperMessage(messages);
    }

    public WrapperMessage getMessages(BigInteger startHistoryId) throws IOException {
        ListHistoryResponse response = gmail.users()
                .history()
                .list("me")
                .setStartHistoryId(startHistoryId)
                .execute();

        List<History> histories = new ArrayList<>();
        while (response.getHistory() != null) {
            histories.addAll(response.getHistory());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = gmail.users()
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
            List<Message> added = history.getMessagesAdded().stream()
                    .map(HistoryMessageAdded::getMessage)
                    .collect(Collectors.toList());
            List<Message> deleted = history.getMessagesDeleted().stream()
                    .map(HistoryMessageDeleted::getMessage)
                    .collect(Collectors.toList());

            messagesAdded.addAll(added);
            messagesDeleted.addAll(deleted);
        }

        return new WrapperMessage(messagesAdded, messagesDeleted);
    }

    class WrapperMessage {
        final private List<Message> addedMessages;
        final private List<Message> deletedMessages;

        WrapperMessage(List<Message> addedMessages) {
            this.addedMessages = addedMessages;
            this.deletedMessages = new ArrayList<>();
        }

        WrapperMessage(List<Message> addedMessages, List<Message> deletedMessages) {
            this.addedMessages = addedMessages;
            this.deletedMessages = deletedMessages;
        }

        public List<Message> getAddedMessages() {
            return addedMessages;
        }

        public List<Message> getDeletedMessages() {
            return deletedMessages;
        }
    }
}
