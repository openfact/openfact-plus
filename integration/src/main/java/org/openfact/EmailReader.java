package org.openfact;

import com.google.api.services.gmail.model.Message;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RecursiveTask;

public class EmailReader extends RecursiveTask<BigDecimal> {

    private final ArrayList<Message> messages;
    private int batchSize;

    public EmailReader(int batchSize) {
        this.batchSize = batchSize;
        messages = new ArrayList<>();
    }

    @Override
    protected BigDecimal compute() {
        return null;
    }

    public boolean add(Message message) {
        return this.messages.add(message);
    }

    public boolean addAll(Collection<Message> messages) {
        return this.messages.addAll(messages);
    }

}
