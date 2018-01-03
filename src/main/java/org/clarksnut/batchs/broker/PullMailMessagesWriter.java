package org.clarksnut.batchs.broker;

import org.clarksnut.documents.DocumentProviderType;
import org.clarksnut.managers.DocumentManager;
import org.clarksnut.managers.exceptions.DocumentNotImportedButSavedForFutureException;
import org.clarksnut.managers.exceptions.DocumentNotImportedException;
import org.clarksnut.repositories.user.MailUblMessageModel;
import org.jboss.logging.Logger;

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
            } catch (DocumentNotImportedException | DocumentNotImportedButSavedForFutureException e) {
                logger.warn("Document not imported");
            }
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return readPosition;
    }

}
