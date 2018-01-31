package org.clarksnut.documents.jpa;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentModel.DocumentCreationEvent;
import org.clarksnut.documents.DocumentModel.DocumentRemovedEvent;
import org.clarksnut.documents.DocumentProvider;
import org.clarksnut.documents.ImportedDocumentModel;
import org.clarksnut.documents.ImportedDocumentStatus;
import org.clarksnut.documents.exceptions.UnreadableDocumentException;
import org.clarksnut.documents.exceptions.UnsupportedDocumentTypeException;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.jpa.entity.DocumentVersionEntity;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.mapper.document.DocumentMapperProvider;
import org.clarksnut.mapper.document.DocumentMapperProviderFactory;
import org.jboss.logging.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class JpaDocumentProvider implements DocumentProvider {

    private static final Logger logger = Logger.getLogger(JpaDocumentProvider.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    @ConfigurationValue("clarksnut.document.mapper.default")
    private Optional<String> clarksnutDocumentMapperDefault;

    @Inject
    private Event<DocumentCreationEvent> creationEvent;

    @Inject
    private Event<DocumentRemovedEvent> removedEvent;

    private String defaultDocumentMapperGroup;

    @PostConstruct
    public void init() {
        defaultDocumentMapperGroup = clarksnutDocumentMapperDefault.orElse("basic");
    }

    private DocumentMapped readDocument(XmlUBLFileModel file) throws UnsupportedDocumentTypeException {
        DocumentMapperProvider mapperProvider = DocumentMapperProviderFactory
                .getInstance()
                .getParsedDocumentProvider(defaultDocumentMapperGroup, file.getDocumentType());
        if (mapperProvider == null && !defaultDocumentMapperGroup.equals("basic")) {
            mapperProvider = DocumentMapperProviderFactory
                    .getInstance()
                    .getParsedDocumentProvider("basic", file.getDocumentType());
        }
        if (mapperProvider == null) {
            throw new UnsupportedDocumentTypeException("Could not find a DocumentMapperProvider for " +
                    "group[" + defaultDocumentMapperGroup + "/basic] " +
                    "documentType[" + file.getDocumentType() + "]");
        }

        return mapperProvider.map(file);
    }

    public static DocumentVersionEntity toDocumentVersionEntity(DocumentBean bean) {
        DocumentVersionEntity entity = new DocumentVersionEntity();

        entity.setAmount(bean.getAmount());
        entity.setTax(bean.getTax());
        entity.setCurrency(bean.getCurrency());
        entity.setIssueDate(bean.getIssueDate());

        entity.setSupplierName(bean.getSupplierName());
        entity.setSupplierStreetAddress(bean.getSupplierStreetAddress());
        entity.setSupplierCity(bean.getSupplierCity());
        entity.setSupplierCountry(bean.getSupplierCountry());

        entity.setCustomerName(bean.getCustomerName());
        entity.setCustomerAssignedId(bean.getCustomerAssignedId());
        entity.setCustomerStreetAddress(bean.getCustomerStreetAddress());
        entity.setCustomerCity(bean.getCustomerCity());
        entity.setCustomerCountry(bean.getCustomerCountry());

        return entity;
    }

    @Override
    public DocumentModel addDocument(ImportedDocumentModel importedDocument, XmlUBLFileModel file) throws UnsupportedDocumentTypeException, UnreadableDocumentException {
        DocumentMapped mappedDocument = readDocument(file);
        if (mappedDocument == null) {
            throw new UnreadableDocumentException("Mapper was not able to read XMLUBLFile[" + file.getDocumentType() + "]");
        }

        final DocumentBean documentBean = mappedDocument.getBean();

        DocumentModel document = getDocument(file.getDocumentType(), documentBean.getAssignedId(), documentBean.getSupplierAssignedId());
        if (document == null) {
            DocumentEntity documentEntity = new DocumentEntity();
            documentEntity.setId(UUID.randomUUID().toString());
            documentEntity.setType(file.getDocumentType());
            documentEntity.setAssignedId(documentBean.getAssignedId());
            documentEntity.setSupplierAssignedId(documentBean.getSupplierAssignedId());
            em.persist(documentEntity);

            DocumentVersionEntity documentVersionEntity = toDocumentVersionEntity(documentBean);
            documentVersionEntity.setId(UUID.randomUUID().toString());
            documentVersionEntity.setCurrentVersion(true);
            documentVersionEntity.setDocument(documentEntity);
            documentVersionEntity.setImportedFile(ImportedDocumentAdapter.toEntity(importedDocument, em));
            em.persist(documentVersionEntity);

            document = new DocumentAdapter(em, documentEntity);

            DocumentModel documentCreated = document;
            creationEvent.fire(new DocumentCreationEvent() {
                @Override
                public String getDocumentType() {
                    return file.getDocumentType();
                }

                @Override
                public Object getJaxb() {
                    return mappedDocument.getType();
                }

                @Override
                public DocumentModel getCreatedDocument() {
                    return documentCreated;
                }
            });
        } else {
            byte[] current = document.getCurrentVersion()
                    .getImportedDocument()
                    .getFile()
                    .getFileAsBytes();
            if (!Arrays.equals(current, file.getFileAsBytes())) {
                DocumentVersionEntity documentVersionEntity = toDocumentVersionEntity(documentBean);
                documentVersionEntity.setId(UUID.randomUUID().toString());
                documentVersionEntity.setCurrentVersion(false);
                documentVersionEntity.setDocument(DocumentAdapter.toEntity(document, em));
                documentVersionEntity.setImportedFile(ImportedDocumentAdapter.toEntity(importedDocument, em));
                em.persist(documentVersionEntity);

                importedDocument.setStatus(ImportedDocumentStatus.IMPORTED);
            } else {
                importedDocument.setStatus(ImportedDocumentStatus.ALREADY_IMPORTED);
            }
        }

        importedDocument.setDocumentReferenceId(document.getId());
        return document;
    }

    @Override
    public DocumentModel getDocument(String id) {
        DocumentEntity entity = em.find(DocumentEntity.class, id);
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

        removedEvent.fire(() -> document);
        return true;
    }

}
