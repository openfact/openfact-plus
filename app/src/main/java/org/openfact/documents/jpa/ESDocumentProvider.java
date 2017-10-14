package org.openfact.documents.jpa;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.hibernate.search.elasticsearch.ElasticsearchQueries;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.engine.spi.QueryDescriptor;
import org.jboss.logging.Logger;
import org.openfact.documents.*;
import org.openfact.documents.DocumentModel.DocumentCreationEvent;
import org.openfact.documents.DocumentModel.DocumentRemovedEvent;
import org.openfact.documents.exceptions.PreexistedDocumentException;
import org.openfact.documents.exceptions.UnreadableDocumentException;
import org.openfact.documents.exceptions.UnsupportedDocumentTypeException;
import org.openfact.documents.jpa.entity.DocumentEntity;
import org.openfact.documents.reader.ReaderUtil;
import org.openfact.files.XmlUBLFileModel;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
public class ESDocumentProvider implements DocumentProvider {

    private static final Logger logger = Logger.getLogger(ESDocumentProvider.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private ReaderUtil readerUtil;

    @Override
    public DocumentModel addDocument(XmlUBLFileModel file, DocumentProviderType providerType) throws UnsupportedDocumentTypeException, UnreadableDocumentException, PreexistedDocumentException {
        GenericDocument genericDocument = readDocument(file);
        if (genericDocument == null) {
            throw new UnreadableDocumentException(file.getDocumentType() + " Is supported but could not be read");
        }
        final Object jaxb = genericDocument.getJaxb();

        DocumentEntity documentEntity = genericDocument.getEntity();
        checkPreExistingDocuments(documentEntity, providerType);

        documentEntity.setId(UUID.randomUUID().toString());
        documentEntity.setType(file.getDocumentType());
        documentEntity.setProviderType(providerType);
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

    private GenericDocument readDocument(XmlUBLFileModel file) throws UnsupportedDocumentTypeException {
        SortedSet<DocumentReader> readers = readerUtil.getReader(file.getDocumentType());
        if (readers.isEmpty()) {
            throw new UnsupportedDocumentTypeException("Unsupported type=" + file.getDocumentType());
        }

        GenericDocument genericDocument = null;
        for (DocumentReader reader : readers) {
            genericDocument = reader.read(file);
            if (genericDocument != null) {
                break;
            }
        }
        return genericDocument;
    }

    private void checkPreExistingDocuments(DocumentEntity documentEntity, DocumentProviderType providerType) throws PreexistedDocumentException {
        DocumentModel document = getDocument(documentEntity.getType(), documentEntity.getAssignedId(), documentEntity.getSupplierAssignedId());
        if (document != null) {
            switch (providerType) {
                case APPLICATION:
                    removeDocument(document);
                    break;
                case USER:
                    if (documentEntity.getProviderType().equals(DocumentProviderType.USER) || documentEntity.getProviderType().equals(DocumentProviderType.MAIL)) {
                        removeDocument(document);
                    } else {
                        throw new PreexistedDocumentException("There is a preexisted document created by an application, you can't override it");
                    }
                    break;
                case MAIL:
                    if (documentEntity.getProviderType().equals(DocumentProviderType.MAIL)) {
                        removeDocument(document);
                    } else {
                        throw new PreexistedDocumentException("There is a preexisted document created by an application, you can't override it");
                    }
                default:
                    throw new IllegalStateException("Unsupported document provider type=" + providerType);
            }
        }
    }

    @Override
    public DocumentModel getDocument(String documentId) {
        DocumentEntity entity = em.find(DocumentEntity.class, documentId);
        if (entity == null) return null;
        return new DocumentAdapter(em, entity);
    }

    @Override
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

    @Override
    public boolean removeDocument(DocumentModel document) {
        DocumentEntity entity = em.find(DocumentEntity.class, document);
        if (entity == null) return false;
        em.remove(entity);

        Event<DocumentRemovedEvent> event = readerUtil.getRemovedEvents(document.getType());
        event.fire(() -> document);
        return true;
    }

    @Override
    public List<DocumentModel> getDocuments() {
        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);
        QueryDescriptor queryDescriptor = ElasticsearchQueries.fromQueryString("");

        QueryBuilder qb = QueryBuilders.termQuery("", "");

        return (List<DocumentModel>) fullTextEm.createFullTextQuery(queryDescriptor, DocumentEntity.class)
                .getResultList().stream()
                .map(f -> new DocumentAdapter(em, (DocumentEntity) f))
                .collect(Collectors.toList());
    }

}
