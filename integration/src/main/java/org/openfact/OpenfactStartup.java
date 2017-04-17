package org.openfact;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import org.jboss.logging.Logger;
import org.openfact.models.DocumentProvider;
import org.openfact.models.OpenfactProvider;
import org.openfact.syncronization.SyncronizationModel;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class OpenfactStartup {

    private static final Logger logger = Logger.getLogger(OpenfactStartup.class);

    private static final int GMAIL_BATCH_SIZE = 100;

    @Inject
    private GmailStartup gmail;

    @Inject
    private DocumentProvider documentProvider;

    @Inject
    private OpenfactProvider openfactProvider;

    @PostConstruct
    private void init() {
        try {
            this.fullSync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processSuccessReadMessage(Message message, HttpHeaders responseHeaders, boolean isAdded) {
        /*SyncronizationModel syncronizationModel = openfactProvider.getSyncronizationModel();
        BigInteger historyId = syncronizationModel.getHistoryId();
        if (historyId == null) {
            syncronizationModel.setHistoryId(message.getHistoryId());
        } else if (historyId.compareTo(message.getHistoryId()) < 0) {
            syncronizationModel.setHistoryId(message.getHistoryId());
        }*/
        System.out.println(message.getHistoryId());
    }

    private void processFailureReadMessage(GoogleJsonError error, HttpHeaders responseHeaders) {
        System.out.println(error);
    }

    public void fullSync() throws IOException {
        try {
            // Get all messages
            ListMessagesResponse response = gmail.getService().users()
                    .messages()
                    .list("me")
                    .execute();

            List<Message> messages = new ArrayList<>();
            while (response.getMessages() != null) {
                messages.addAll(response.getMessages());
                if (response.getNextPageToken() != null) {
                    String pageToken = response.getNextPageToken();
                    response = gmail.getService().users()
                            .messages()
                            .list("me")
                            .setPageToken(pageToken)
                            .execute();
                } else {
                    break;
                }
            }

            batchAddedMessages(messages, true);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read credentials");
        }
    }

    public void partialSync(BigInteger startHistoryId) throws IOException {
        ListHistoryResponse response = gmail.getService().users()
                .history()
                .list("me")
                .setStartHistoryId(startHistoryId)
                .execute();

        List<History> histories = new ArrayList<>();
        while (response.getHistory() != null) {
            histories.addAll(response.getHistory());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = gmail.getService().users()
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

        batchAddedMessages(messagesAdded, true);
        batchAddedMessages(messagesDeleted, false);
    }

    private void batchAddedMessages(List<Message> messages, boolean isAdded) {
        if (!messages.isEmpty()) {
            ForkJoinPool pool = new ForkJoinPool();
            GmailBatch task = new GmailBatch(gmail.getService(), isAdded, messages, 0, messages.size() - 1, GMAIL_BATCH_SIZE);
            pool.invoke(task);
        }
    }

    class GmailBatch extends RecursiveAction {

        private final Gmail gmailService;
        private boolean isAdded;
        private final int threshold;
        private final List<Message> messages;
        private int start;
        private int end;

        public GmailBatch(Gmail gmailService, boolean isAdded, List<Message> messages, int start, int end, int threshold) {
            this.gmailService = gmailService;
            this.isAdded = isAdded;
            this.threshold = threshold;
            this.messages = messages;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start < threshold) {
                try {
                    JsonBatchCallback<Message> callback = new JsonBatchCallback<Message>() {
                        @Override
                        public void onSuccess(Message message, HttpHeaders responseHeaders) throws IOException {
                            processSuccessReadMessage(message, responseHeaders, isAdded);
                        }

                        @Override
                        public void onFailure(GoogleJsonError error, HttpHeaders responseHeaders) throws IOException {
                            processFailureReadMessage(error, responseHeaders);
                        }
                    };

                    BatchRequest batch = gmailService.batch();

                    for (int i = start; i <= end; i++) {
                        Message message = messages.get(i);
                        gmailService.users().messages().get("me", message.getId()).queue(batch, callback);
                    }

                    batch.execute();
                } catch (IOException e) {
                    logger.error("Error reading email", e);
                }
            } else {
                int midway = (end - start) / 2 + start;
                GmailBatch a1 = new GmailBatch(gmailService, isAdded, messages, start, midway, threshold);
                GmailBatch a2 = new GmailBatch(gmailService, isAdded, messages, midway + 1, end, threshold);

                a1.fork();
                a2.fork();
                a1.join();
                a2.join();
            }
        }

    }

}
