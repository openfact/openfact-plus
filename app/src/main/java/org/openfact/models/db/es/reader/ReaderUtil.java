package org.openfact.models.db.es.reader;

import org.openfact.models.DocumentModel.DocumentCreationEvent;
import org.openfact.models.DocumentModel.DocumentRemovedEvent;
import org.openfact.models.db.es.DocumentReader;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Stateless
public class ReaderUtil {

    @Inject
    @Any
    private Instance<DocumentReader> readers;

    @Inject
    private Event<DocumentCreationEvent> creationEvents;

    @Inject
    private Event<DocumentRemovedEvent> removedEvents;

    private final Lock lock = new ReentrantLock();
    private Map<String, SortedSet<DocumentReader>> cacheReaders;

    public SortedSet<DocumentReader> getReader(String documentType) {
        if (cacheReaders == null) {
            lock.lock();
            try {
                cacheReaders = new HashMap<>();
                for (DocumentReader reader : readers) {
                    MapperType mapper = reader.getClass().getAnnotation(MapperType.class);
                    String mapperValue = mapper.value();

                    SortedSet<DocumentReader> readers;
                    if (cacheReaders.containsKey(mapperValue)) {
                        readers = cacheReaders.get(mapperValue);
                    } else {
                        readers = new TreeSet<>((r1, r2) -> (r1.getPriority() > r2.getPriority() ? -1 : (r1 == r2 ? 0 : 1)));
                    }
                    readers.add(reader);
                    cacheReaders.put(mapperValue, readers);
                }
            } finally {
                lock.unlock();
            }
        }
        return cacheReaders.get(documentType);
    }

    public Event<DocumentCreationEvent> getCreationEvents(String documentType) {
        Annotation mapperTypeLiteral = new MapperTypeLiteral(documentType);
        return creationEvents.select(mapperTypeLiteral);
    }

    public Event<DocumentRemovedEvent> getRemovedEvents(String documentType) {
        Annotation mapperTypeLiteral = new MapperTypeLiteral(documentType);
        return removedEvents.select(mapperTypeLiteral);
    }

}
