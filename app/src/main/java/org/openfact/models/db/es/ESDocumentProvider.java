package org.openfact.models.db.es;

import org.jboss.logging.Logger;
import org.openfact.models.*;
import org.openfact.models.DocumentModel.DocumentCreationEvent;
import org.openfact.models.DocumentModel.DocumentRemovedEvent;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.reader.ReaderUtil;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.SortedSet;

@Stateless
public class ESDocumentProvider implements DocumentProvider {

    private static final Logger logger = Logger.getLogger(ESDocumentProvider.class);

    @Inject
    private EntityManager em;

    @Inject
    private ReaderUtil readerUtil;

    @Override
    public DocumentModel addDocument(XmlUBLFileModel file) throws ModelUnsupportedTypeException, ModelParseException {
        SortedSet<DocumentReader> readers = readerUtil.getReader(file.getDocumentType());
        if (readers.isEmpty()) {
            throw new ModelUnsupportedTypeException("Unsupported type=" + file.getDocumentType());
        }

        GenericDocument genericDocument = null;
        for (DocumentReader reader : readers) {
            genericDocument = reader.read(file);
            if (genericDocument != null) {
                break;
            }
        }
        if (genericDocument == null) {
            throw new ModelParseException(file.getDocumentType() + " Is supported but could not parsed");
        }
        Object jaxb = genericDocument.getJaxb();

        DocumentEntity documentEntity = genericDocument.getEntity();
        documentEntity.setId(OpenfactModelUtils.generateId());
        documentEntity.setType(file.getDocumentType());
        em.persist(documentEntity);

        DocumentAdapter document = new DocumentAdapter(em, documentEntity);

        Event<DocumentCreationEvent> event = readerUtil.getCreationEvents(document.getType());
        event.fire(new DocumentCreationEvent() {
            @Override
            public String getDocumentType() {
                return file.getDocumentType();
            }

            @Override
            public Object getJaxb() {
                return jaxb;
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

        Event<DocumentRemovedEvent> event = readerUtil.getRemovedEvents(document.getType());
        event.fire(() -> document);
        return true;
    }

}
