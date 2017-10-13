package org.openfact.models.db.search.reader;

import org.openfact.models.DocumentModel.DocumentCreationEvent;
import org.openfact.models.DocumentModel.DocumentRemovedEvent;
import org.openfact.models.db.search.DocumentReader;

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
                    String supportedDocumentType = reader.getSupportedDocumentType();
                    SortedSet<DocumentReader> readers;
                    if (cacheReaders.containsKey(supportedDocumentType)) {
                        readers = cacheReaders.get(supportedDocumentType);
                    } else {
                        readers = new TreeSet<>((r1, r2) -> (r1.getPriority() > r2.getPriority() ? -1 : (r1 == r2 ? 0 : 1)));
                    }
                    readers.add(reader);
                    cacheReaders.put(supportedDocumentType, readers);
                }
            } finally {
                lock.unlock();
            }
        }
        return cacheReaders.get(documentType);
    }

    public Event<DocumentCreationEvent> getCreationEvents(String documentType) {
        Annotation mapperTypeLiteral = new SupportedTypeLiteral(documentType);
        return creationEvents.select(mapperTypeLiteral);
    }

    public Event<DocumentRemovedEvent> getRemovedEvents(String documentType) {
        Annotation mapperTypeLiteral = new SupportedTypeLiteral(documentType);
        return removedEvents.select(mapperTypeLiteral);
    }

}
