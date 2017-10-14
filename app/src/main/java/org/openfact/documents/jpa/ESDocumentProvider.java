package org.openfact.documents.jpa;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.hibernate.search.elasticsearch.ElasticsearchQueries;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.engine.spi.QueryDescriptor;
import org.jboss.logging.Logger;
import org.openfact.documents.DocumentModel;
import org.openfact.documents.DocumentModel.DocumentCreationEvent;
import org.openfact.documents.DocumentModel.DocumentRemovedEvent;
import org.openfact.documents.DocumentProvider;
import org.openfact.documents.DocumentReader;
import org.openfact.documents.GenericDocument;
import org.openfact.documents.exceptions.UnreadableDocumentException;
import org.openfact.documents.exceptions.UnsupportedDocumentTypeException;
import org.openfact.documents.jpa.entity.DocumentEntity;
import org.openfact.documents.reader.ReaderUtil;
import org.openfact.files.XmlUBLFileModel;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

@Stateless
public class ESDocumentProvider implements DocumentProvider {

    private static final Logger logger = Logger.getLogger(ESDocumentProvider.class);

    @Inject
    private EntityManager em;

    @Inject
    private ReaderUtil readerUtil;

    @Override
    public DocumentModel addDocument(XmlUBLFileModel file) throws UnsupportedDocumentTypeException, UnreadableDocumentException {
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
        if (genericDocument == null) {
            throw new UnreadableDocumentException(file.getDocumentType() + " Is supported but could not parsed");
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
