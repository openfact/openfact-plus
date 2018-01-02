package org.clarksnut.batchs.support.io;

import org.jberet.support.io.JpaItemWriter;

import javax.inject.Named;
import java.util.List;

@Named
public class JpaMixedWriter extends JpaItemWriter {

    @Override
    public void writeItems(final List<Object> items) throws Exception {
        if (entityTransaction) {
            em.getTransaction().begin();
        }
        for (final Object e : items) {
            if (!(e instanceof MixedObject)) {
                throw new IllegalStateException("To use Multiple writer you should send MixedObject");
            }

            MixedObject item = (MixedObject) e;
            switch (item.getOperation().toLowerCase()) {
                case "persist":
                    em.persist(item.getObject());
                    break;
                case "merge":
                    em.merge(item.getObject());
                    break;
                case "remove":
                    em.remove(item.getObject());
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
