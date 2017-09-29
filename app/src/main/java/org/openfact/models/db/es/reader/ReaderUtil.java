package org.openfact.models.db.es.reader;

import org.keycloak.Config;
import org.openfact.OpenfactConfig;
import org.openfact.models.DocumentModel;
import org.openfact.models.DocumentModel.DocumentCreationEvent;
import org.openfact.models.DocumentModel.DocumentRemovedEvent;
import org.openfact.models.db.es.DocumentReader;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.annotation.Annotation;

@Stateless
public class ReaderUtil {

    @Inject
    @Any
    private Instance<DocumentReader> providers;

    @Inject
    private Event<DocumentCreationEvent> creationEvents;

    @Inject
    private Event<DocumentRemovedEvent> removedEvents;

    public ReaderUtil() {
    }

    public DocumentReader getReader(String documentType) {
        String location = OpenfactConfig.getInstance()
                .getProperty(documentType.toLowerCase(), "default");
        return getReader(documentType, location);
    }

    public DocumentReader getReader(String documentType, String location) {
        Annotation mapperTypeLiteral = new MapperTypeLiteral(documentType);
        Annotation locationTypeLiteral = new LocationTypeLiteral(location);

        Instance<DocumentReader> instance = providers.select(mapperTypeLiteral, locationTypeLiteral);
        if (!instance.isAmbiguous() && !instance.isUnsatisfied()) {
            return instance.get();
        }
        return null;
    }

    public Event<DocumentCreationEvent> getCreationEvents(String documentType) {
        String location = OpenfactConfig.getInstance()
                .getProperty(documentType.toLowerCase(), "default");
        return getCreationEvents(documentType, location);
    }

    public Event<DocumentCreationEvent> getCreationEvents(String documentType, String location) {
        Annotation mapperTypeLiteral = new MapperTypeLiteral(documentType);
        Annotation locationTypeLiteral = new LocationTypeLiteral(location);
        return creationEvents.select(mapperTypeLiteral, locationTypeLiteral);
    }

    public Event<DocumentRemovedEvent> getRemovedEvents(String documentType) {
        String location = OpenfactConfig.getInstance()
                .getProperty(documentType.toLowerCase(), "default");
        return getRemovedEvents(documentType, location);
    }

    public Event<DocumentRemovedEvent> getRemovedEvents(String documentType, String location) {
        Annotation mapperTypeLiteral = new MapperTypeLiteral(documentType);
        Annotation locationTypeLiteral = new LocationTypeLiteral(location);
        return removedEvents.select(mapperTypeLiteral, locationTypeLiteral);
    }

}
