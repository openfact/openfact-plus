package org.clarksnut.batchs.support.io;

import org.jberet.support.io.JpaItemWriter;

import javax.batch.api.BatchProperty;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
public class JpaAdvancedWriter extends JpaItemWriter {

    @Inject
    @BatchProperty
    protected String operation;

    @Override
    public void writeItems(final List<Object> items) throws Exception {
        if (entityTransaction) {
            em.getTransaction().begin();
        }
        if (operation == null) {
            operation = "persist";
        }
        for (final Object e : items) {
            switch (operation.toLowerCase()) {
                case "persist":
                    em.persist(e);
                    break;
                case "merge":
                    em.merge(e);
                    break;
                case "remove":
                    em.remove(e);
                    break;
                default:
                    throw new IllegalStateException("Operation not supported");
            }
        }
        if (entityTransaction) {
            em.getTransaction().commit();
        }
    }

}
