package org.openfact.models.db.es;

import org.jboss.logging.Logger;
import org.openfact.models.*;
import org.openfact.models.DocumentModel.DocumentCreationEvent;
import org.openfact.models.DocumentModel.DocumentRemovedEvent;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.reader.MapperTypeLiteral;
import org.openfact.models.db.es.reader.ReaderUtil;
import org.openfact.models.utils.OpenfactModelUtils;

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
    private ReaderUtil readerUtil;

    private String getDocumentType(FileModel file) {
        String documentType;
        try {
            documentType = OpenfactModelUtils.getDocumentType(file.getFile());
        } catch (Exception e) {
            throw new ModelException("Could not read file bytes");
        }
        return documentType;
    }

    @Override
    public DocumentModel addDocument(XmlUblFileModel file) throws ModelUnsupportedTypeException, ModelFetchException, ModelParseException {
        String documentType = getDocumentType(file);
        DocumentReader reader = readerUtil.getReader(documentType);
        if (reader == null) {
            throw new ModelUnsupportedTypeException("Unsupported type=" + documentType);
        }

        GenericDocument genericDocument = reader.read(file);
        if (genericDocument == null) {
            throw new ModelException("Could not read all required fields on Invoice");
        }

        DocumentEntity documentEntity = genericDocument.getEntity();
        documentEntity.setId(OpenfactModelUtils.generateId());
        documentEntity.setType(documentType);
        em.persist(documentEntity);

        DocumentAdapter document = new DocumentAdapter(em, documentEntity);

        Event<DocumentCreationEvent> event = readerUtil.getCreationEvents(document.getType());
        event.fire(new DocumentCreationEvent() {
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

        Event<DocumentRemovedEvent> event = readerUtil.getRemovedEvents(document.getType());
        event.fire(() -> document);
        return true;
    }

}
