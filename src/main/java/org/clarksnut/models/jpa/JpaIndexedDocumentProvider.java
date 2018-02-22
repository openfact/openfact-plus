package org.clarksnut.models.jpa;

import org.clarksnut.models.DocumentModel;
import org.clarksnut.models.DocumentModel.DocumentCreationEvent;
import org.clarksnut.models.DocumentVersionModel;
import org.clarksnut.models.jpa.entity.DocumentEntity;
import org.clarksnut.models.jpa.entity.DocumentVersionEntity;
import org.clarksnut.models.jpa.entity.IndexedDocumentEntity;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class JpaIndexedDocumentProvider {

    @PersistenceContext
    private EntityManager em;

    public void index(@Observes DocumentCreationEvent documentCreationEvent) {
        DocumentModel document = documentCreationEvent.getCreatedDocument();
        DocumentVersionModel currentVersion = document.getCurrentVersion();

        DocumentEntity documentEntity = DocumentAdapter.toEntity(document, em);
        DocumentVersionEntity documentVersionEntity = DocumentVersionAdapter.toEntity(currentVersion, em);

        IndexedDocumentEntity entity = new IndexedDocumentEntity();
        entity.setId(document.getId());
        entity.setType(document.getType());
        entity.setAssignedId(document.getAssignedId());
        entity.setSupplierAssignedId(document.getSupplier().getAssignedId());


        entity.setIssueDate(documentVersionEntity.getIssueDate());
        entity.setCurrency(documentVersionEntity.getCurrency());
        entity.setAmount(documentVersionEntity.getAmount());
        entity.setTax(documentVersionEntity.getTax());
        entity.setSupplierName(documentVersionEntity.getSupplierName());
        entity.setSupplierStreetAddress(documentVersionEntity.getSupplierStreetAddress());
        entity.setSupplierCity(documentVersionEntity.getSupplierCity());
        entity.setSupplierCountry(documentVersionEntity.getSupplierCountry());
        entity.setCustomerAssignedId(documentVersionEntity.getCustomerAssignedId());
        entity.setCustomerName(documentVersionEntity.getCustomerName());
        entity.setCustomerStreetAddress(documentVersionEntity.getCustomerStreetAddress());
        entity.setCustomerCity(documentVersionEntity.getCustomerCity());
        entity.setCustomerCountry(documentVersionEntity.getCustomerCountry());
        entity.setCreatedAt(documentVersionEntity.getCreatedAt());
        entity.setUpdatedAt(documentVersionEntity.getUpdatedAt());

        entity.setDocument(documentEntity);
        em.persist(entity);
    }

}
