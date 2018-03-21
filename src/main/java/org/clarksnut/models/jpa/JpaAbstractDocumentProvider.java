package org.clarksnut.models.jpa;

import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.models.DocumentModel;
import org.clarksnut.models.ImportedDocumentModel;
import org.clarksnut.models.exceptions.AlreadyImportedDocumentException;
import org.clarksnut.models.jpa.entity.DocumentEntity;
import org.clarksnut.models.jpa.entity.DocumentVersionEntity;
import org.jboss.logging.Logger;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class JpaAbstractDocumentProvider {

    private static final Logger logger = Logger.getLogger(JpaLuceneDocumentProvider.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Event<DocumentModel.DocumentCreationEvent> creationEvent;

    @Inject
    private Event<DocumentModel.DocumentRemovedEvent> removedEvent;

    public DocumentModel addDocument(String documentType, ImportedDocumentModel importedDocument, DocumentMapped.DocumentBean bean)
            throws AlreadyImportedDocumentException {
        DocumentModel document = getDocument(documentType, bean.getAssignedId(), bean.getSupplierAssignedId());
        if (document == null) {
            DocumentEntity documentEntity = new DocumentEntity();
            documentEntity.setId(UUID.randomUUID().toString());
            documentEntity.setType(documentType);
            documentEntity.setAssignedId(bean.getAssignedId());

            documentEntity.setIssueDate(bean.getIssueDate());
            documentEntity.setCurrency(bean.getCurrency());
            documentEntity.setAmount(bean.getAmount());
            documentEntity.setTax(bean.getTax());

            documentEntity.setSupplierName(bean.getSupplierName());
            documentEntity.setSupplierAssignedId(bean.getSupplierAssignedId());
            documentEntity.setSupplierStreetAddress(bean.getSupplierStreetAddress());
            documentEntity.setSupplierCity(bean.getSupplierCity());
            documentEntity.setSupplierCountry(bean.getSupplierCountry());

            documentEntity.setCustomerName(bean.getCustomerName());
            documentEntity.setCustomerAssignedId(bean.getCustomerAssignedId());
            documentEntity.setCustomerStreetAddress(bean.getCustomerStreetAddress());
            documentEntity.setCustomerCity(bean.getCustomerCity());
            documentEntity.setCustomerCountry(bean.getCustomerCountry());
            em.persist(documentEntity);

            DocumentVersionEntity documentVersionEntity = new DocumentVersionEntity();
            documentVersionEntity.setId(UUID.randomUUID().toString());
            documentVersionEntity.setCurrentVersion(true);
            documentVersionEntity.setDocument(documentEntity);
            documentVersionEntity.setImportedDocument(ImportedDocumentAdapter.toEntity(importedDocument, em));
            em.persist(documentVersionEntity);

            document = new DocumentAdapter(em, documentEntity);
            logger.debug("New Document has been imported");

            final DocumentModel documentCreated = document;
            creationEvent.fire(() -> documentCreated);
        } else {
            long currentChecksum = document.getCurrentVersion().getImportedDocument().getFile().getChecksum();
            if (currentChecksum != importedDocument.getFile().getChecksum()) {
                DocumentVersionEntity documentVersionEntity = new DocumentVersionEntity();
                documentVersionEntity.setId(UUID.randomUUID().toString());
                documentVersionEntity.setCurrentVersion(false);
                documentVersionEntity.setDocument(DocumentAdapter.toEntity(document, em));
                documentVersionEntity.setImportedDocument(ImportedDocumentAdapter.toEntity(importedDocument, em));
                em.persist(documentVersionEntity);

                logger.debug("New Document Version has been imported");
            } else {
                throw new AlreadyImportedDocumentException("Document has already been imported");
            }
        }

        return document;
    }

    public DocumentModel getDocument(String id) {
        DocumentEntity entity = em.find(DocumentEntity.class, id);
        if (entity == null) return null;
        return new DocumentAdapter(em, entity);
    }

    public DocumentModel getDocumentViewAndChecksAndStarts(String id) {
        EntityGraph<?> graph = em.getEntityGraph("graph.DocumentViewsAndChecks");

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.fetchgraph", graph);

        DocumentEntity entity = em.find(DocumentEntity.class, id, properties);
        if (entity == null) return null;
        return new DocumentAdapter(em, entity);
    }

    public DocumentModel getDocument(String type, String assignedId, String supplierAssignedId) {
        TypedQuery<DocumentEntity> typedQuery = em.createNamedQuery("getDocumentByTypeAssignedIdAndSupplierAssignedId", DocumentEntity.class);
        typedQuery.setParameter("type", type);
        typedQuery.setParameter("assignedId", assignedId);
        typedQuery.setParameter("supplierAssignedId", supplierAssignedId);

        List<DocumentEntity> resultList = typedQuery.getResultList();
        if (resultList.size() == 1) {
            return new DocumentAdapter(em, resultList.get(0));
        } else if (resultList.size() == 0) {
            return null;
        } else {
            throw new IllegalStateException("Invalid number of results");
        }
    }

    public boolean removeDocument(DocumentModel document) {
        DocumentEntity entity = em.find(DocumentEntity.class, document.getId());
        if (entity == null) return false;
        em.remove(entity);
        em.flush();

        removedEvent.fire(() -> document);
        return true;
    }

}
