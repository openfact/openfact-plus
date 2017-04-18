package org.openfact;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.common.collect.Lists;
import org.jboss.logging.Logger;
import org.openfact.models.DocumentProvider;
import org.openfact.models.ModelException;
import org.openfact.models.OpenfactProvider;
import org.openfact.syncronization.SyncronizationModel;
import org.openfact.util.FindMaxHistoryIdTask;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class OpenfactService {

    private static final Logger logger = Logger.getLogger(OpenfactService.class);

    private static final int BATCH_SIZE = 1000;

    @Resource
    private ManagedExecutorService executorService;

    @Inject
    private GmailClientService gmailService;

    @Inject
    private OpenfactProvider openfactProvider;

    @Inject
    private DocumentProvider documentProvider;

    @PostConstruct
    private void init() {
        SyncronizationModel syncronizationModel = openfactProvider.getSyncronizationModel();
        BigInteger startHistoryId = syncronizationModel.getHistoryId();
        try {
            synchronize(startHistoryId);
        } catch (ModelException e) {
            logger.error("Startup error, could not read messages");
        }
    }

    public Optional<BigInteger> synchronize(BigInteger startHistoryId) throws ModelException {
        Gmail gmailClient = gmailService.getClientService();

        GmailSync gmailSync = new GmailSync(gmailClient);
        GmailSync.WrapperMessage messages;
        if (startHistoryId != null) {
            try {
                messages = gmailSync.getMessages(startHistoryId);
            } catch (IOException e) {
                logger.warn("Could not retrieve messages from Partial Sync StartHistoryId[" + startHistoryId + "]");
                logger.info("Trying to retrieve messages on Full Sync mode...");
                try {
                    messages = gmailSync.getMessages();
                } catch (IOException e1) {
                    logger.error("Could not retrieve messages from Full Sync mode");
                    throw new ModelException("Could not read messages on both Full and Partial Sync modes");
                }
            }
        } else {
            try {
                messages = gmailSync.getMessages();
            } catch (IOException e) {
                logger.error("Could not retrieve messages from Full Sync mode");
                throw new ModelException("Could not read messages on Full Sync mode");
            }
        }

        // Process added and deleted messages
        List<CompletableFuture<List<Message>>> addedFutures = new ArrayList<>();
        List<CompletableFuture<List<Message>>> deletedFutures = new ArrayList<>();
        for (List<Message> chunkMessages : Lists.partition(messages.getAddedMessages(), BATCH_SIZE)) {
            addedFutures.add(CompletableFuture.supplyAsync(execBatch(chunkMessages, gmailClient), executorService));
        }
        for (List<Message> chunkMessages : Lists.partition(messages.getDeletedMessages(), BATCH_SIZE)) {
            deletedFutures.add(CompletableFuture.supplyAsync(execBatch(chunkMessages, gmailClient), executorService));
        }

        try {
            CompletableFuture[] addedCompletableFutures = addedFutures.toArray(new CompletableFuture[addedFutures.size()]);
            List<Message> addedMessages = CompletableFuture.allOf(addedCompletableFutures)
                    .thenApply(v -> addedFutures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList())
                    ).get().stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            // Finding last historyId
            ForkJoinPool pool = new ForkJoinPool();
            FindMaxHistoryIdTask task = new FindMaxHistoryIdTask(addedMessages, 0, addedMessages.size()-1, addedMessages.size()/16);

            CompletableFuture[] deletedCompletableFutures = deletedFutures.toArray(new CompletableFuture[deletedFutures.size()]);
            List<Message> deletedMessages = CompletableFuture.allOf(addedCompletableFutures)
                    .thenApply(v -> deletedFutures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList())
                    ).get().stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());


        } catch (InterruptedException | ExecutionException e) {
            logger.error("Batch execution exception, could not complete reading messages", e);
            throw new ModelException("Batch execution exception, could not complete reading messages", e);
        }

        return null;
    }

   /* private static void findLargestFuture(List<Message> messages) throws Exception {
        List<Future<Long>> futures = new ArrayList<>();

        for (int start = 0; start < messages.size(); start++) {
            final int s = start;
            if (messages.get(s).getHistoryId().compareTo(BigInteger.ZERO) > 0) {
                futures.add(CompletableFuture.supplyAsync(() -> {
                    BigInteger maxresult = messages.get(s).getHistoryId(); //seed
                    for (int end = s; end < messages.size(); end++) {
                        BigInteger testvalue = messages.get(end).getHistoryId();
                        if (testvalue.compareTo(BigInteger.ZERO) > 0) {
                            List<Message> tester = messages.subList(s, end + 1);
                            long testresult = sum(tester);
                            numTries.increment();
                            if (maxresult < testresult) {
                                maxresult = testresult;
                            }
                        } else if (testvalue > maxresult) {
                            maxresult = testvalue;
                        }
                    }
                    return maxresult;
                });
            }
        }
        long result = futures.stream()
                .map(f -> {
                    try {
                        return f.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }).max(Long::compare)
                // if all values are negative, get the biggest one
                .orElseGet(() ->(long)messages.stream().max(Integer::compare).get());
        System.out.println("Total execution time: " + (System.currentTimeMillis() - starttime));
        System.out.println("Largest Sum: " + result);
        System.out.println("Number of searches " + numTries);
    }*/

    private Supplier<List<Message>> execBatch(List<Message> messages, Gmail gmail) {
        return () -> {
            List<Message> result = new ArrayList<>();
            try {
                BatchRequest batch = gmail.batch();
                for (Message message : messages) {
                    gmail.users().messages().get("me", message.getId()).queue(batch, new JsonBatchCallback<Message>() {
                        @Override
                        public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                            logger.error("Read Message Failure code=" + e.getCode() + " message=" + e.getMessage());
                        }

                        @Override
                        public void onSuccess(Message message, HttpHeaders responseHeaders) throws IOException {
                            result.add(message);
                        }
                    });
                }
                batch.execute();
            } catch (IOException e) {
                logger.error("Error reading messages batch", e);
                throw new IllegalStateException("Error reading messages batch", e);
            }
            return result;
        };
    }


}
