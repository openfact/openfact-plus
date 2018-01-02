package org.clarksnut.documents.parser;

import org.clarksnut.documents.DocumentModel;

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
public class ParsedDocumentProviderFactory {

    @Inject
    @Any
    private Instance<ParsedDocumentProvider> readers;

    @Inject
    private Event<DocumentModel.DocumentCreationEvent> creationEvents;

    @Inject
    private Event<DocumentModel.DocumentRemovedEvent> removedEvents;

    private final Lock lock = new ReentrantLock();
    private Map<String, SortedSet<ParsedDocumentProvider>> cacheReaders;

    public SortedSet<ParsedDocumentProvider> getParser(String documentType) {
        if (cacheReaders == null) {
            lock.lock();
            try {
                cacheReaders = new HashMap<>();
                for (ParsedDocumentProvider reader : readers) {
                    String supportedDocumentType = reader.getSupportedDocumentType();
                    SortedSet<ParsedDocumentProvider> readers;
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

    public Event<DocumentModel.DocumentCreationEvent> getCreationEvents(String documentType) {
        Annotation mapperTypeLiteral = new SupportedDocumentTypeLiteral(documentType);
        return creationEvents.select(mapperTypeLiteral);
    }

    public Event<DocumentModel.DocumentRemovedEvent> getRemovedEvents(String documentType) {
        Annotation mapperTypeLiteral = new SupportedDocumentTypeLiteral(documentType);
        return removedEvents.select(mapperTypeLiteral);
    }

}
