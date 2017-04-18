package org.openfact.models.jpa;

import org.jboss.logging.Logger;
import org.openfact.models.*;
import org.openfact.models.jpa.entities.DocumentEntity;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class JpaDocumentProvider implements DocumentProvider {

    protected static final Logger logger = Logger.getLogger(JpaDocumentProvider.class);

    public static final String ACCOUNTING_CUSTOMER_PARTY_ID = "accountingCustomerPartyId";
    public static final String DOCUMENT_ID = "documentId";
    public static final String DOCUMENT_TYPE = "documentType";
    public static final String CREATED_TIMESTAMP = "createdTimestamp";
    public static final String DOCUMENT_CURRENCY_CODE = "documentCurrencyCode";
    public static final String CUSTOMER_REGISTRATION_NAME = "customerRegistrationName";
    public static final String CUSTOMER_ASSIGNED_ACCOUNT_ID = "customerAssignedAccountId";
    public static final String CUSTOMER_ELECTRONIC_MAIL = "customerElectronicMail";
    public static final String REQUIRED_ACTIONS = "requiredActions";
    public static final String ENABLED = "enabled";

    public static final String SEND_EVENT_DESTINY = "destiny";
    public static final String SEND_EVENT_STATUS = "status";
    public static final String CUSTOMER_SEND_EVENT_FAILURES = "customerSendEventFailures";
    public static final String THIRD_PARTY_SEND_EVENT_FAILURES = "thirdPartySendEventFailures";

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Event<DocumentModel> event;

    @Override
    public DocumentQuery createQuery(AccountingCustomerPartyModel customerParty) {
        return new JpaDocumentQuery(em, customerParty);
    }

    @Override
    public DocumentModel addDocument(String documentType, String documentId) throws ModelException {
        DocumentEntity entity = new DocumentEntity();
        entity.setDocumentType(documentType.toUpperCase());
        entity.setDocumentId(documentId.toUpperCase());
        em.persist(entity);
        em.flush();

        final DocumentModel adapter = new DocumentAdapter(em, entity);
        event.fire(adapter);

        logger.debug("Document documentId[" + documentId + "] created");
        return adapter;
    }

    @Override
    public DocumentModel getDocumentById(String id, AccountingCustomerPartyModel customerParty) {
        /*DocumentEntity entity = em.find(DocumentEntity.class, id);
        if (entity == null) return null;
        return new DocumentAdapter(customerParty, em, entity);*/
        return null;
    }

    @Override
    public void preRemove(AccountingCustomerPartyModel customerParty) {
        em.createNamedQuery("deleteDocumentLineAttributesByAccountingCustomerParty").setParameter("accountingCustomerPartyId", customerParty.getId()).executeUpdate();
        em.createNamedQuery("deleteDocumentLinesByAccountingCustomerParty").setParameter("accountingCustomerPartyId", customerParty.getId()).executeUpdate();
        em.createNamedQuery("deleteDocumentAttributesByAccountingCustomerParty").setParameter("accountingCustomerPartyId", customerParty.getId()).executeUpdate();
        em.createNamedQuery("deleteDocumentsByAccountingCustomerParty").setParameter("accountingCustomerPartyId", customerParty.getId()).executeUpdate();
    }

    @Override
    public boolean removeDocument(String id) {
        DocumentEntity entity = em.find(DocumentEntity.class, id);
        if (entity == null) return false;
        em.remove(entity);
        return true;
    }

    @Override
    public List<DocumentModel> getDocuments(AccountingCustomerPartyModel customerParty) {
        return getDocuments(customerParty, -1, -1);
    }

    @Override
    public List<DocumentModel> getDocuments(AccountingCustomerPartyModel customerParty, int firstResult, int maxResults) {
        /*TypedQuery<DocumentEntity> query = em.createNamedQuery("getAllDocumentsByAccountingCustomerParty", DocumentEntity.class);
        query.setParameter("accountingCustomerPartyId", customerParty.getId());
        if (firstResult != -1) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }
        return query.getResultList().stream()
                .map(entity -> new DocumentAdapter(customerParty, em, entity))
                .collect(Collectors.toList());*/
        return null;
    }
}
