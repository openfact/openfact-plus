package org.openfact.batchs.broker;

import org.jberet.support.io.JpaItemReaderWriterBase;
import org.jboss.logging.Logger;
import org.openfact.documents.DocumentProviderType;
import org.openfact.repositories.user.MailUblMessageModel;
import org.openfact.services.managers.DocumentManager;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemWriter;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
public class PullMailMessagesWriter extends JpaItemReaderWriterBase implements ItemWriter {

    private static final Logger logger = Logger.getLogger(PullMailMessagesWriter.class);

    @Inject
    private DocumentManager documentManager;

    /**
     * Flag to control whether to begin entity transaction before writing items,
     * and to commit entity transaction after writing items.
     * Optional property, and defaults to {@code false}.
     */
    @Inject
    @BatchProperty
    protected boolean entityTransaction;

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
        if (entityTransaction) {
            em.getTransaction().begin();
        }

        while (readPosition < items.size()) {
            MailUblMessageModel message = (MailUblMessageModel) items.get(readPosition++);
            documentManager.importDocument(message.getXml(), DocumentProviderType.MAIL);
        }

        if (entityTransaction) {
            em.getTransaction().commit();
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return readPosition;
    }
}
