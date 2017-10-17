package org.openfact.batchs.broker;

import org.jberet.support.io.JpaItemReaderWriterBase;
import org.jboss.logging.Logger;

import javax.batch.api.BatchProperty;
import javax.batch.api.listener.StepListener;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class PullMailMessagesStepListener extends JpaItemReaderWriterBase implements StepListener {

    private static final Logger logger = Logger.getLogger(PullMailMessagesStepListener.class);

    @Inject
    private LinkedBrokers linkedBrokers;

    /**
     * Flag to control whether to begin entity transaction before writing items,
     * and to commit entity transaction after writing items.
     * Optional property, and defaults to {@code false}.
     */
    @Inject
    @BatchProperty
    protected boolean entityTransaction;

    @Override
    public void beforeStep() throws Exception {
        
    }

    @Override
    public void afterStep() throws Exception {
        if (entityTransaction) {
            em.getTransaction().begin();
        }

//        for (final Object e : linkedBrokers.getLinkedBrokers()) {
//            em.merge(e);
//        }

        if (entityTransaction) {
            em.getTransaction().commit();
        }
    }

}
