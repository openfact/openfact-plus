package org.clarksnut.documents.jpa;

import org.apache.lucene.search.Sort;
import org.clarksnut.documents.*;
import org.clarksnut.documents.DocumentModel.DocumentCreationEvent;
import org.clarksnut.documents.DocumentModel.DocumentRemovedEvent;
import org.clarksnut.documents.exceptions.PreexistedDocumentException;
import org.clarksnut.documents.exceptions.UnreadableDocumentException;
import org.clarksnut.documents.exceptions.UnsupportedDocumentTypeException;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.reader.ReaderUtil;
import org.clarksnut.files.XmlUBLFileModel;
import org.hibernate.search.elasticsearch.ElasticsearchQueries;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.sort.SortFieldContext;
import org.hibernate.search.query.engine.spi.QueryDescriptor;
import org.jboss.logging.Logger;

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
        documentEntity.setId(UUID.randomUUID().toString());
        documentEntity.setType(file.getDocumentType());
        documentEntity.setProvider(providerType);
        documentEntity.setFileId(file.getId());

        checkPreExistingDocuments(documentEntity, providerType);

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
                    if (documentEntity.getProvider().equals(DocumentProviderType.USER) || documentEntity.getProvider().equals(DocumentProviderType.MAIL)) {
                        removeDocument(document);
                    } else {
                        throw new PreexistedDocumentException("There is a preexisted document created by an application, you can't override it");
                    }
                    break;
                case MAIL:
                    throw new PreexistedDocumentException("There is a preexisted document imported by another mail, you can't override it");
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
        DocumentEntity entity = em.find(DocumentEntity.class, document.getId());
        if (entity == null) return false;
        em.remove(entity);
        em.flush();

        Event<DocumentRemovedEvent> event = readerUtil.getRemovedEvents(document.getType());
        event.fire(() -> document);
        return true;
    }

    @Override
    public List<DocumentModel> getDocuments(DocumentQueryModel query) {
        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);
        QueryDescriptor queryDescriptor;
        if (query.isJsonQuery()) {
            queryDescriptor = ElasticsearchQueries.fromJson(query.getQuery());
        } else {
            queryDescriptor = ElasticsearchQueries.fromQueryString(query.getQuery());
        }

        Sort sort = null;
        if (query.getOrderBy() != null) {
            QueryBuilder queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(DocumentEntity.class).get();
            SortFieldContext sortFieldContext = queryBuilder.sort().byField(query.getOrderBy());
            if (query.isAsc()) {
                sort = sortFieldContext.asc().createSort();
            } else {
                sort = sortFieldContext.desc().createSort();
            }
        }

        FullTextQuery fullTextQuery = fullTextEm.createFullTextQuery(queryDescriptor, DocumentEntity.class);
        if (sort != null) {
            fullTextQuery.setSort(sort);
        }

        if (query.getOffset() != null && query.getOffset() != -1) {
            fullTextQuery.setFirstResult(query.getOffset());
        }
        if (query.getLimit() != null && query.getLimit() != -1) {
            fullTextQuery.setMaxResults(query.getLimit());
        }

        return (List<DocumentModel>) fullTextQuery.getResultList().stream()
                .map(f -> new DocumentAdapter(em, (DocumentEntity) f))
                .collect(Collectors.toList());
    }

    @Override
    public int getDocumentsSize(DocumentQueryModel query) {
        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);
        QueryDescriptor queryDescriptor;
        if (query.isJsonQuery()) {
            queryDescriptor = ElasticsearchQueries.fromJson(query.getQuery());
        } else {
            queryDescriptor = ElasticsearchQueries.fromQueryString(query.getQuery());
        }

        return fullTextEm.createFullTextQuery(queryDescriptor, DocumentEntity.class)
                .getResultSize();
    }


}
