package org.openfact.models.jpa;

import org.jboss.logging.Logger;
import org.openfact.models.DocumentModel;
import org.openfact.models.DocumentProvider;
import org.openfact.models.DocumentQuery;
import org.openfact.models.ModelException;
import org.openfact.models.jpa.entities.DocumentEntity;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class JpaDocumentProvider implements DocumentProvider {

    protected static final Logger logger = Logger.getLogger(JpaDocumentProvider.class);

    public static final String SUPPLIER_PARTY_ASSIGNED_ACCOUNT_ID = "supplierPartyAssignedAccountId";
    public static final String CUSTOMER_PARTY_ASSIGNED_ACCOUNT_ID = "customerPartyAssignedAccountId";

    public static final String DOCUMENT_ID = "documentId";
    public static final String DOCUMENT_TYPE = "documentType";
    public static final String CREATED_TIMESTAMP = "createdTimestamp";
    public static final String DOCUMENT_CURRENCY_CODE = "documentCurrencyCode";
    public static final String ENABLED = "enabled";

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Event<DocumentModel> event;

    @Override
    public DocumentQuery createQuery() {
        return new JpaDocumentQuery(em);
    }

    @Override
    public DocumentModel addDocument(String documentType, String documentId, String supplierAssignedAccountId, String originUuid) throws ModelException {
        DocumentEntity entity = new DocumentEntity();
        entity.setDocumentType(documentType.toUpperCase());
        entity.setDocumentId(documentId.toUpperCase());
        entity.setSupplierPartyAssignedAccountId(supplierAssignedAccountId);
        entity.setOriginUuid(originUuid);
        em.persist(entity);
        em.flush();

        final DocumentModel adapter = new DocumentAdapter(em, entity);
        event.fire(adapter);

        logger.debug("Document documentId[" + documentId + "] created");
        return adapter;
    }

    @Override
    public DocumentModel getDocumentById(String id) {
        DocumentEntity entity = em.find(DocumentEntity.class, id);
        if (entity == null) return null;
        return new DocumentAdapter(em, entity);
    }

    @Override
    public DocumentModel getDocumentByTypeIdAndSupplierAssignedAccountId(String documentType, String documentId, String supplierAssignedAccountId) throws ModelException {
        TypedQuery<DocumentEntity> query = em.createNamedQuery("getDocumentByTypeIdAndSupplierAssignedAccountId", DocumentEntity.class);
        query.setParameter("documentType", documentType);
        query.setParameter("documentId", documentId);
        query.setParameter("supplierPartyAssignedAccountId", supplierAssignedAccountId);
        List<DocumentEntity> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else if (resultList.size() > 1) {
            throw new ModelException("More than one documents asserts the search conditions");
        } else {
            return new DocumentAdapter(em, resultList.get(0));
        }
    }

    @Override
    public DocumentModel getDocumentByOriginUuid(String originUuid) throws ModelException {
        TypedQuery<DocumentEntity> query = em.createNamedQuery("getDocumentByOriginUuid", DocumentEntity.class);
        query.setParameter("originUuid", originUuid);
        List<DocumentEntity> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else if (resultList.size() > 1) {
            throw new ModelException("More than one documents asserts the search conditions");
        } else {
            return new DocumentAdapter(em, resultList.get(0));
        }
    }

    @Override
    public boolean removeDocument(String id) {
        DocumentEntity entity = em.find(DocumentEntity.class, id);
        if (entity == null) return false;
        em.remove(entity);
        return true;
    }

    @Override
    public List<DocumentModel> getDocumentsBySupplier(String supplierAssignedAccountId) {
        return getDocumentsBySupplier(supplierAssignedAccountId, -1, -1);
    }

    @Override
    public List<DocumentModel> getDocumentsBySupplier(String supplierAssignedAccountId, int firstResult, int maxResults) {
        TypedQuery<DocumentEntity> query = em.createNamedQuery("getAllDocumentsByAccountingSupplierParty", DocumentEntity.class);
        query.setParameter("supplierPartyAssignedAccountId", supplierAssignedAccountId);
        if (firstResult != -1) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }
        return query.getResultList().stream()
                .map(entity -> new DocumentAdapter(em, entity))
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentModel> getDocumentsByCustomer(String supplierAssignedAccountId) {
        return getDocumentsByCustomer(supplierAssignedAccountId, -1, -1);
    }

    @Override
    public List<DocumentModel> getDocumentsByCustomer(String customerAssignedAccountId, int firstResult, int maxResults) {
        TypedQuery<DocumentEntity> query = em.createNamedQuery("getAllDocumentsByAccountingCustomerParty", DocumentEntity.class);
        query.setParameter("customerPartyAssignedAccountId", customerAssignedAccountId);
        if (firstResult != -1) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }
        return query.getResultList().stream()
                .map(entity -> new DocumentAdapter(em, entity))
                .collect(Collectors.toList());
    }

}
