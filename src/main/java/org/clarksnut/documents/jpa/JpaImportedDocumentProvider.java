package org.clarksnut.documents.jpa;

import org.clarksnut.documents.DocumentProviderType;
import org.clarksnut.documents.ImportedDocumentModel;
import org.clarksnut.documents.ImportedDocumentProvider;
import org.clarksnut.documents.jpa.entity.ImportedDocumentEntity;
import org.clarksnut.files.CompressedFileModel;
import org.clarksnut.files.jpa.FileAdapter;
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
    public ImportedDocumentModel importDocument(CompressedFileModel file, DocumentProviderType provider) {

        ImportedDocumentEntity importedParentDocumentEntity = new ImportedDocumentEntity();
        importedParentDocumentEntity.setId(UUID.randomUUID().toString());
        importedParentDocumentEntity.setFilename(file.getFilename());
        importedParentDocumentEntity.setFile(FileAdapter.toEntity(file, em));
        importedParentDocumentEntity.setProvider(provider);
        em.persist(importedParentDocumentEntity);

        if (file.isCompressed()) {
            file.getChildrenIfExists().forEach(fileEntry -> {
                ImportedDocumentEntity importedChildDocumentEntity = new ImportedDocumentEntity();
                importedChildDocumentEntity.setId(UUID.randomUUID().toString());
                importedChildDocumentEntity.setFilename(fileEntry.getFilename());
                importedChildDocumentEntity.setFile(FileAdapter.toEntity(fileEntry, em));
                importedChildDocumentEntity.setProvider(provider);
                importedChildDocumentEntity.setParent(importedChildDocumentEntity);
                em.persist(importedChildDocumentEntity);
            });
        }

        return new ImportedDocumentAdapter(em, importedParentDocumentEntity);
    }

    @Override
    public ImportedDocumentModel getImportedDocument(String documentId) {
        ImportedDocumentEntity entity = em.find(ImportedDocumentEntity.class, documentId);
        if (entity == null) return null;
        return new ImportedDocumentAdapter(em, entity);
    }

    @Override
    public boolean removeImportedDocument(ImportedDocumentModel document) {
        ImportedDocumentEntity entity = em.find(ImportedDocumentEntity.class, document.getId());
        if (entity == null) return false;
        em.remove(entity);

        return true;
    }

}
