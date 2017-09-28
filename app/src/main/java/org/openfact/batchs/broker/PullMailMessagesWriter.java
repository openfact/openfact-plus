package org.openfact.batchs.broker;

import org.jboss.logging.Logger;
import org.openfact.models.ModelUnsupportedTypeException;
import org.openfact.services.managers.DocumentManager;

import javax.batch.api.chunk.ItemWriter;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
public class PullMailMessagesWriter implements ItemWriter {

    private static final Logger logger = Logger.getLogger(PullMailMessagesWriter.class);

    @Inject
    private DocumentManager documentManager;

    @Override
    public void open(Serializable checkpoint) throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void writeItems(List<Object> items) throws Exception {
        for (Object o : items) {
            PullMailMessageWrapper wrapper = (PullMailMessageWrapper) o;
            try {
                documentManager.importDocument(wrapper.getMessage().getXml());
            } catch (ModelUnsupportedTypeException e) {
                logger.warn("Unsupported document type", e);
            }
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }
}
