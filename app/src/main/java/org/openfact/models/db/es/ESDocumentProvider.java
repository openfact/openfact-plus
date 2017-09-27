package org.openfact.models.db.es;

import org.jboss.logging.Logger;
import org.openfact.models.DocumentModel;
import org.openfact.models.DocumentModel.DocumentCreationEvent;
import org.openfact.models.DocumentModel.DocumentRemovedEvent;
import org.openfact.models.DocumentProvider;
import org.openfact.models.FileModel;
import org.openfact.models.ModelException;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.reader.MapperTypeLiteral;
import org.openfact.models.utils.OpenfactModelUtils;
import org.w3c.dom.Document;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.lang.annotation.Annotation;

@Stateless
public class ESDocumentProvider implements DocumentProvider {

    private static final Logger logger = Logger.getLogger(ESDocumentProvider.class);

    @Inject
    private EntityManager em;

    @Inject
    @Any
    private Instance<DocumentReader> documentReaders;

    @Inject
    private Event<DocumentCreationEvent> creationEvents;

    @Inject
    private Event<DocumentRemovedEvent> removedEvents;

    private String getDocumentType(FileModel file) {
        String documentType;
        try {
            documentType = OpenfactModelUtils.getDocumentType(file.getFile());
        } catch (Exception e) {
            throw new ModelException("Could not read file bytes");
        }
        return documentType;
    }

    private DocumentReader getDocumentReader(String documentType) {
        Annotation annotation = new MapperTypeLiteral(documentType);
        Instance<DocumentReader> instance = documentReaders.select(annotation);
        if (instance.isAmbiguous() || instance.isUnsatisfied()) {
            logger.warn("Could not find a reader for:" + documentType);
            return null;
        }
        return instance.get();
    }

    private <T> Event<T> getProviderEvent(String documentType, Event<T> events) {
        Annotation annotation = new MapperTypeLiteral(documentType);
        return events.select(annotation);
    }

    @Override
    public DocumentModel addDocument(FileModel file) {
        String documentType = getDocumentType(file);
        DocumentReader documentReader = getDocumentReader(documentType);
        GenericDocument genericDocument = documentReader.read(file);
        if (genericDocument == null) {
            throw new ModelException("Could not read all required fields on Invoice");
        }

        DocumentEntity documentEntity = genericDocument.getEntity();
        documentEntity.setId(OpenfactModelUtils.generateId());
        documentEntity.setType(documentType);
        em.persist(documentEntity);

        DocumentAdapter document = new DocumentAdapter(em, documentEntity);
        getProviderEvent(documentType, creationEvents).fire(new DocumentCreationEvent() {
            @Override
            public String getType() {
                return documentType;
            }

            @Override
            public Object getDocumentType() {
                return genericDocument.getType();
            }

            @Override
            public DocumentModel getCreatedDocument() {
                return document;
            }
        });

        return document;
    }

    @Override
    public DocumentModel getDocument(String documentId) {
        DocumentEntity entity = em.find(DocumentEntity.class, documentId);
        if (entity == null) return null;
        return new DocumentAdapter(em, entity);
    }

    @Override
    public boolean removeDocument(DocumentModel document) {
        DocumentEntity entity = em.find(DocumentEntity.class, document);
        if (entity == null) return false;
        em.remove(entity);

        getProviderEvent(document.getType(), removedEvents).fire(new DocumentRemovedEvent() {
            @Override
            public DocumentModel getDocument() {
                return document;
            }
        });
        return true;
    }

    @Override
    public boolean isSupported(String documentType) {
        return getDocumentReader(documentType) != null;
    }

    @Override
    public boolean isSupported(byte[] bytes) {
        try {
            return isSupported(OpenfactModelUtils.getDocumentType(bytes));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isSupported(Document document) {
        try {
            return isSupported(OpenfactModelUtils.getDocumentType(document));
        } catch (Exception e) {
            return false;
        }
    }

}
