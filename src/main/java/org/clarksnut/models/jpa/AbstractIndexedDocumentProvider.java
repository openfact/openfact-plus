package org.clarksnut.models.jpa;

import org.clarksnut.models.IndexedDocumentModel;
import org.clarksnut.models.jpa.entity.IndexedDocumentEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public abstract class AbstractIndexedDocumentProvider {

    protected abstract EntityManager getEntityManager();

    public IndexedDocumentModel getIndexedDocument(String documentId) {
        TypedQuery<IndexedDocumentEntity> typedQuery = getEntityManager().createNamedQuery("getIndexedDocumentById", IndexedDocumentEntity.class);
        typedQuery.setParameter("documentId", documentId);

        List<IndexedDocumentEntity> resultList = typedQuery.getResultList();
        if (resultList.isEmpty()) return null;
        else if (resultList.size() > 1) throw new IllegalStateException("Invalid number of results");
        else return new IndexedDocumentAdapter(getEntityManager(), resultList.get(0));
    }

}
