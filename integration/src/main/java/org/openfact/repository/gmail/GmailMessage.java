package org.openfact.repository.gmail;

import com.google.api.services.gmail.model.Message;

import java.util.ArrayList;
import java.util.List;

public class GmailMessage {

    final private List<Message> addedMessages;
    final private List<Message> deletedMessages;

    public GmailMessage(List<Message> addedMessages) {
        this.addedMessages = addedMessages;
        this.deletedMessages = new ArrayList<>();
    }

    public GmailMessage(List<Message> addedMessages, List<Message> deletedMessages) {
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
