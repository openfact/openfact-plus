package org.openfact.services.managers;

import com.google.api.services.gmail.model.Message;

import java.util.ArrayList;
import java.util.List;

public class GmailWrappedMessage {

    final private List<Message> addedMessages;
    final private List<Message> deletedMessages;

    GmailWrappedMessage(List<Message> addedMessages) {
        this.addedMessages = addedMessages;
        this.deletedMessages = new ArrayList<>();
    }

    GmailWrappedMessage(List<Message> addedMessages, List<Message> deletedMessages) {
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
