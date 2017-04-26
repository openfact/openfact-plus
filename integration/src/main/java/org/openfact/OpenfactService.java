package org.openfact;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.common.collect.Lists;
import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.debitnote_21.DebitNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.jboss.logging.Logger;
import org.openfact.common.converts.DocumentUtils;
import org.openfact.models.DocumentModel;
import org.openfact.models.DocumentProvider;
import org.openfact.models.ModelException;
import org.openfact.models.OpenfactProvider;
import org.openfact.models.types.DocumentType;
import org.openfact.services.managers.DocumentManager;
import org.openfact.services.managers.GmailManager;
import org.openfact.services.managers.GmailWrappedMessage;
import org.openfact.syncronization.SyncronizationModel;
import org.openfact.util.FindMaxHistoryIdTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    private GmailManager gmailManager;

    @Inject
    private OpenfactProvider openfactProvider;

    @Inject
    private DocumentProvider documentProvider;

    @Inject
    private DocumentManager documentManager;

    @PostConstruct
    private void init() {
        SyncronizationModel syncronizationModel = openfactProvider.getSyncronizationModel();
        BigInteger startHistoryId = syncronizationModel.getHistoryId();
        try {
            Optional<BigInteger> lastHistoryId = synchronize(startHistoryId);
            syncronizationModel.setHistoryId(lastHistoryId.orElseGet(() -> null));
        } catch (ModelException e) {
            logger.error("Startup error, could not read messages");
        }
    }

    public Optional<BigInteger> synchronize(BigInteger startHistoryId) throws ModelException {
        Gmail gmailClient = gmailService.getClientService();

        String query = "filename:xml";

        GmailWrappedMessage messages;
        if (startHistoryId != null) {
            try {
                messages = gmailManager.getMessages(startHistoryId);
            } catch (IOException e) {
                logger.warn("Could not retrieve messages from Partial Sync StartHistoryId[" + startHistoryId + "]");
                try {
                    logger.info("Trying to retrieve messages on Full Sync mode...");
                    messages = gmailManager.getMessages(query);
                } catch (IOException e1) {
                    logger.error("Could not retrieve messages from Full Sync mode");
                    throw new ModelException("Could not read messages on both Full and Partial Sync modes");
                }
            }
        } else {
            try {
                messages = gmailManager.getMessages(query);
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

        List<Message> addedMessages;
        List<Message> deletedMessages;
        try {
            addedMessages = asyn(addedFutures).get().stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            deletedMessages = asyn(deletedFutures).get().stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Batch execution exception, could not complete reading messages", e);
            throw new ModelException("Batch execution exception, could not complete reading messages", e);
        }

        processAddedMessages(addedMessages);
        processDeletedMessages(deletedMessages);

        if (!addedMessages.isEmpty()) {
            ForkJoinPool pool = new ForkJoinPool();
            int threshold = addedMessages.size() > 100 ? addedMessages.size() / 16 : 1;
            FindMaxHistoryIdTask task = new FindMaxHistoryIdTask(addedMessages, 0, addedMessages.size() - 1, threshold);
            BigInteger maxHistoryid = pool.invoke(task);
            if (maxHistoryid.compareTo(BigInteger.ZERO) > 0) {
                return Optional.of(pool.invoke(task));
            }
        }

        return Optional.empty();
    }

    private void processAddedMessages(List<Message> messages) throws ModelException {
        try {
            for (Message message : messages) {
                List<MessagePart> parts = message.getPayload().getParts();
                for (MessagePart part : parts) {
                    String filename = part.getFilename();
                    if (filename != null && filename.length() > 0 && (filename.endsWith(".xml") || filename.endsWith(".XML"))) {
                        String attachmentId = part.getBody().getAttachmentId();
                        MessagePartBody messagePartBody = gmailService.getClientService().users().messages().attachments().get("me", message.getId(), attachmentId).execute();

                        Base64 base64url = new Base64(true);
                        byte[] fileByteArray = base64url.decodeBase64(messagePartBody.getData());

                        Document xml;
                        try {
                            xml = DocumentUtils.byteToDocument(fileByteArray);
                        } catch (Exception e) {
                            logger.error("Error transforming bytes to xml");
                            continue;
                        }
                        Element documentElement = xml.getDocumentElement();
                        int items = documentElement.getElementsByTagName("ds:Signature").getLength();
                        if (items > 0) {
                            boolean findId = ((Element) documentElement.getElementsByTagName("ds:Signature").item(0)).hasAttribute("Id");
                            if (findId) {
                                String id = ((Element) documentElement.getElementsByTagName("ds:Signature").item(0)).getAttributeNode("Id").getNodeValue();
                                if (id.startsWith("#")) {
                                    ((Element) documentElement.getElementsByTagName("ds:Signature").item(0)).getAttributeNode("Id").setNodeValue(id.substring(1));
                                }
                                String documentType = documentElement.getTagName();
                                switch (documentType) {
                                    case "Invoice":
                                        InvoiceType invoiceType = UBL21Reader.invoice().read(xml);
                                        if (invoiceType != null) {
                                            if (documentProvider.getDocumentByTypeIdAndSupplierAssignedAccountId(DocumentType.INVOICE.toString(), invoiceType.getIDValue(), DocumentManager.buildSupplierPartyAssignedAccountId(invoiceType)) == null) {
                                                documentManager.addDocument(invoiceType, message.getId());
                                            } else {
                                                logger.warn("Tried to create Invoice that already exists");
                                            }
                                        }
                                        break;
                                    case "CreditNote":
                                        CreditNoteType creditNoteType = UBL21Reader.creditNote().read(xml);
                                        if (creditNoteType != null) {
                                            if (documentProvider.getDocumentByTypeIdAndSupplierAssignedAccountId(DocumentType.CREDIT_NOTE.toString(), creditNoteType.getIDValue(), DocumentManager.buildSupplierPartyAssignedAccountId(creditNoteType)) == null) {
                                                documentManager.addDocument(creditNoteType, message.getId());
                                            } else {
                                                logger.warn("Tried to create CreditNote that already exists");
                                            }
                                        }
                                        break;
                                    case "DebitNote":
                                        DebitNoteType debitNoteType = UBL21Reader.debitNote().read(xml);
                                        if (debitNoteType != null) {
                                            if (documentProvider.getDocumentByTypeIdAndSupplierAssignedAccountId(DocumentType.DEBIT_NOTE.toString(), debitNoteType.getIDValue(), DocumentManager.buildSupplierPartyAssignedAccountId(debitNoteType)) == null) {
                                                documentManager.addDocument(debitNoteType, message.getId());
                                            } else {
                                                logger.warn("Tried to create DebitNote that already exists");
                                            }
                                        }
                                        break;
                                    default:
                                        logger.warn("Invalid DocumentType to read");
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error reading attached data", e);
            throw new ModelException("Error reading attached data", e);
        }
    }

    private void processDeletedMessages(List<Message> messages) {
        for (Message message : messages) {
            DocumentModel document = null;
            try {
                document = documentProvider.getDocumentByOriginUuid(message.getId());
                if (document != null) {
                    documentProvider.removeDocument(document.getId());
                } else {
                    logger.warn("Tried to delete a document that was not persisted");
                }
            } catch (ModelException e) {
                logger.error("Error searching for document", e);
            }
        }
    }

    private CompletableFuture<List<List<Message>>> asyn(List<CompletableFuture<List<Message>>> futures) throws ExecutionException, InterruptedException {
        CompletableFuture[] completableFutures = futures.toArray(new CompletableFuture[futures.size()]);
        return CompletableFuture.allOf(completableFutures)
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                );
    }

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
