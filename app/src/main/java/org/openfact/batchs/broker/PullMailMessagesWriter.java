package org.openfact.batchs.broker;

import org.jboss.logging.Logger;
import org.openfact.documents.DocumentProviderType;
import org.openfact.documents.exceptions.PreexistedDocumentException;
import org.openfact.documents.exceptions.UnreadableDocumentException;
import org.openfact.documents.exceptions.UnsupportedDocumentTypeException;
import org.openfact.repositories.user.MailUblMessageModel;
import org.openfact.services.managers.DocumentManager;

import javax.batch.api.chunk.ItemWriter;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
public class PullMailMessagesWriter implements ItemWriter {

    @Inject
    private DocumentManager documentManager;

    private static final Logger logger = Logger.getLogger(PullMailMessagesReader.class);

    /**
     * Current write position
     */
    protected int readPosition;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        if (checkpoint == null) {
            readPosition = 0;
        } else {
            readPosition = (Integer) checkpoint;
        }
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void writeItems(List<Object> items) throws Exception {
        while (readPosition < items.size()) {
            MailUblMessageModel message = (MailUblMessageModel) items.get(readPosition++);
            try {
                documentManager.importDocument(message.getXml(), DocumentProviderType.MAIL);
            } catch (UnsupportedDocumentTypeException e) {
                logger.debug("Unsupported document type");
            } catch (UnreadableDocumentException e) {
                logger.warn("Unreadable document");
            } catch (PreexistedDocumentException e) {
                logger.warn("Preexisted document");
            }
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return readPosition;
    }

}
