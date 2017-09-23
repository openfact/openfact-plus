package org.openfact.batchs.broker;

import org.openfact.repositories.user.MailUBLMessage;

public class PullMailMessageWrapper {

    private final MailUBLMessage message;

    public PullMailMessageWrapper(MailUBLMessage message) {
        this.message = message;
    }

    public MailUBLMessage getMessage() {
        return message;
    }
}
