package org.clarksnut.models.jpa;

import org.clarksnut.files.FileModel;
import org.clarksnut.files.jpa.FileAdapter;
import org.clarksnut.models.DocumentProviderType;
import org.clarksnut.models.ImportedDocumentModel;
import org.clarksnut.models.ImportedDocumentProvider;
import org.clarksnut.models.ImportedDocumentStatus;
import org.clarksnut.models.jpa.entity.ImportedDocumentEntity;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Stateless
public class JpaImportedDocumentProvider implements ImportedDocumentProvider {

    private static final Logger logger = Logger.getLogger(JpaImportedDocumentProvider.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public ImportedDocumentModel importDocument(FileModel file, DocumentProviderType provider) {
        ImportedDocumentEntity entity = new ImportedDocumentEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setProvider(provider);
        entity.setStatus(ImportedDocumentStatus.PENDING);
        entity.setFile(FileAdapter.toEntity(file, em));
        em.persist(entity);
        return new ImportedDocumentAdapter(em, entity);
    }

    @Override
    public ImportedDocumentModel getImportedDocument(String documentId) {
        ImportedDocumentEntity entity = em.find(ImportedDocumentEntity.class, documentId);
        if (entity == null) return null;
        return new ImportedDocumentAdapter(em, entity);
    }

    @Override
    public boolean removeImportedDocument(ImportedDocumentModel importedDocument) {
        em.createNamedQuery("removeImportedDocumentById")
                .setParameter("importedDocumentId", importedDocument.getId())
                .executeUpdate();
        em.createNamedQuery("removeFileByImportedDocumentId")
                .setParameter("importedDocumentId", importedDocument.getId())
                .executeUpdate();
        return true;
    }

}
