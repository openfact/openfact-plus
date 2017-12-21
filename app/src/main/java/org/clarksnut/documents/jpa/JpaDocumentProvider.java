package org.clarksnut.documents.jpa;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentModel.DocumentCreationEvent;
import org.clarksnut.documents.DocumentModel.DocumentRemovedEvent;
import org.clarksnut.documents.DocumentProvider;
import org.clarksnut.documents.DocumentProviderType;
import org.clarksnut.documents.DocumentType;
import org.clarksnut.documents.exceptions.UnreadableDocumentException;
import org.clarksnut.documents.exceptions.UnrecognizableDocumentTypeException;
import org.clarksnut.documents.exceptions.UnsupportedDocumentTypeException;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.documents.jpa.entity.DocumentUtil;
import org.clarksnut.documents.jpa.entity.DocumentVersionEntity;
import org.clarksnut.documents.parser.ParsedDocument;
import org.clarksnut.documents.parser.ParsedDocumentProvider;
import org.clarksnut.documents.parser.ParsedDocumentProviderFactory;
import org.clarksnut.documents.parser.SkeletonDocument;
import org.clarksnut.files.XmlUBLFileModel;
import org.jboss.logging.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;

@Stateless
public class JpaDocumentProvider implements DocumentProvider {

    private static final Logger logger = Logger.getLogger(JpaDocumentProvider.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private ParsedDocumentProviderFactory readerUtil;

    @Inject
    @ConfigurationValue("clarksnut.document.additionalTypes")
    private Optional<String[]> clarksnutAdditionalDocumentTypes;

    @Override
    public DocumentModel addDocument(XmlUBLFileModel file, String fileProvider, boolean isVerified, DocumentProviderType providerType)
            throws UnsupportedDocumentTypeException, UnreadableDocumentException, UnrecognizableDocumentTypeException {

        ParsedDocument parsedDocument = readDocument(file);
        if (parsedDocument == null) {
            throw new UnreadableDocumentException(file.getDocumentType() + " Is supported but could not be read");
        }
        final Object jaxb = parsedDocument.getType();
        final SkeletonDocument skeleton = parsedDocument.getSkeleton();

        DocumentModel document;
        DocumentModel previousDocument = getDocument(file.getDocumentType(), skeleton.getAssignedId(), skeleton.getSupplierAssignedId());
        if (previousDocument == null) {
            DocumentEntity documentEntity = DocumentUtil.toDocumentEntity(skeleton);

            documentEntity.setId(UUID.randomUUID().toString());
            documentEntity.setType(file.getDocumentType());
            documentEntity.setProvider(providerType);
            documentEntity.setFileId(file.getId());
            documentEntity.setFileProvider(fileProvider);
            documentEntity.setVerified(isVerified);

            em.persist(documentEntity);
            document = new DocumentAdapter(em, documentEntity);
        } else {
            document = previousDocument;
        }


        DocumentVersionEntity versionEntity = DocumentUtil.toDocumentVersionEntity(skeleton);

        versionEntity.setId(UUID.randomUUID().toString());
        versionEntity.setType(file.getDocumentType());
        versionEntity.setProvider(providerType);
        versionEntity.setFileId(file.getId());
        versionEntity.setVerified(isVerified);
        versionEntity.setCurrentVersion(previousDocument == null);
        versionEntity.setDocument(DocumentAdapter.toEntity(document, em));

        em.persist(versionEntity);

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

    private ParsedDocument readDocument(XmlUBLFileModel file) throws UnsupportedDocumentTypeException, UnrecognizableDocumentTypeException {
        SortedSet<ParsedDocumentProvider> readers = readerUtil.getParser(file.getDocumentType());
        if (readers.isEmpty()) {
            Optional<DocumentType> optional = DocumentType.getByType(file.getDocumentType());
            if (optional.isPresent()) {
                throw new UnsupportedDocumentTypeException("Unsupported type=" + file.getDocumentType());
            }

            Optional<String> additionalOptional = Arrays.stream(clarksnutAdditionalDocumentTypes.orElse(new String[0])).filter(p -> p.equals(file.getDocumentType())).findFirst();
            if (additionalOptional.isPresent()) {
                throw new UnsupportedDocumentTypeException("Unsupported type=" + file.getDocumentType());
            } else {
                throw new UnrecognizableDocumentTypeException("Unrecognizable type=" + file.getDocumentType());
            }
        }

        ParsedDocument parsedDocument = null;
        for (ParsedDocumentProvider reader : readers) {
            parsedDocument = reader.read(file);
            if (parsedDocument != null) {
                break;
            }
        }

        return parsedDocument;
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

}
