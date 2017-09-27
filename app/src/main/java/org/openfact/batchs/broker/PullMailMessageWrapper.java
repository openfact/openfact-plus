package org.openfact.batchs.broker;

import org.openfact.repositories.user.MailUblMessageModel;

public class PullMailMessageWrapper {

    private final MailUblMessageModel message;

    public PullMailMessageWrapper(MailUblMessageModel message) {
        this.message = message;
    }

    public MailUblMessageModel getMessage() {
        return message;
    }
}
