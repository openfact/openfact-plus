package org.clarksnut.documents.jpa;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentVersionModel;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.jpa.entity.DocumentVersionEntity;
import org.clarksnut.documents.jpa.entity.IndexedDocumentEntity;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Stateless
public class JpaIndexedDocumentProvider {

    @PersistenceContext
    private EntityManager em;

    public void index(@Observes DocumentModel document) {
        DocumentVersionModel currentVersion = document.getCurrentVersion();
        DocumentVersionEntity currentVersionEntity = DocumentVersionAdapter.toEntity(currentVersion, em);

        IndexedDocumentEntity entity = new IndexedDocumentEntity();
        entity.setId(document.getId());
        entity.setAssignedId(document.getAssignedId());
        entity.setSupplierAssignedId(document.getSupplierAssignedId());

        entity.setAmount(currentVersionEntity.getAmount());
        entity.setCurrency(currentVersionEntity.getCurrency());
        
        em.persist(entity);
    }

}
