package org.clarksnut.documents.jpa;

import org.clarksnut.documents.UnsavedDocumentModel;
import org.clarksnut.documents.UnsavedDocumentProvider;
import org.clarksnut.documents.UnsavedReasonType;
import org.clarksnut.documents.jpa.entity.UnsavedDocumentEntity;
import org.clarksnut.files.XmlUBLFileModel;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Stateless
public class JpaUnsavedDocumentProvider implements UnsavedDocumentProvider {

    private static final Logger logger = Logger.getLogger(JpaUnsavedDocumentProvider.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public UnsavedDocumentModel addDocument(XmlUBLFileModel file, UnsavedReasonType reason) {
        UnsavedDocumentEntity entity = new UnsavedDocumentEntity();

        entity.setId(UUID.randomUUID().toString());
        entity.setType(file.getDocumentType());
        entity.setFileId(file.getId());
        entity.setReason(reason);

        em.persist(entity);

        return new UnsavedDocumentAdapter(em, entity);
    }

    @Override
    public UnsavedDocumentModel getDocument(String documentId) {
        UnsavedDocumentEntity entity = em.find(UnsavedDocumentEntity.class, documentId);
        if (entity == null) return null;
        return new UnsavedDocumentAdapter(em, entity);
    }

    @Override
    public boolean removeDocument(UnsavedDocumentModel document) {
        UnsavedDocumentEntity entity = em.find(UnsavedDocumentEntity.class, document.getId());
        if (entity == null) return false;
        em.remove(entity);

        return true;
    }

}
