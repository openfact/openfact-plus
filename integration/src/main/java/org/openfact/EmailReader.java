package org.openfact;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveTask;

public class EmailReader extends RecursiveTask<BigInteger> {

    private static final Logger logger = Logger.getLogger(EmailReader.class);

    private Gmail gmail;

    private final CopyOnWriteArrayList<Message> messages;
    private final int threshold;
    private int start;
    private int end;

    private EmailReader(Gmail gmail, CopyOnWriteArrayList<Message> messages, int start, int end, int threshold) {
        this.gmail = gmail;
        this.messages = messages;
        this.start = start;
        this.end = end;
        this.threshold = threshold;
    }

    public static EmailReader build(Gmail gmail, CopyOnWriteArrayList<Message> messages, int batchSize) {
        return new EmailReader(gmail, new CopyOnWriteArrayList<>(), 0, messages.size() - 1, batchSize);
    }

    @Override
    protected BigInteger compute() {
        if (end - start < threshold) {
            JsonBatchCallback<Message> callback = new JsonBatchCallback<Message>() {
                @Override
                public void onSuccess(Message message, HttpHeaders responseHeaders) throws IOException {
                }

                @Override
                public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                }
            };

            BigInteger max = BigInteger.ZERO;

            try {
                BatchRequest batch = gmail.batch();

                for (int i = start; i <= end; i++) {
                    Message message = messages.get(i);
                    if (message.getHistoryId().compareTo(max) > 0) {
                        max = message.getHistoryId();
                    }

                    gmail.users().messages().get("me", message.getId()).queue(batch, callback);
                }

                batch.execute();
            } catch (IOException e) {
                logger.error("Error reading email", e);
            }

            return max;
        } else {
            int midway = (end - start) / 2 + start;
            final EmailReader firstGroup = new EmailReader(gmail, messages, start, midway, threshold);
            firstGroup.fork();

            final EmailReader restGroup = new EmailReader(gmail, messages, midway + 1, end, threshold);

            BigInteger firstMaxHistoryId = firstGroup.compute();
            BigInteger restMaxHistoryId = restGroup.join();
            return firstMaxHistoryId.compareTo(restMaxHistoryId) > 0 ? firstMaxHistoryId : restMaxHistoryId;
        }
    }

}
