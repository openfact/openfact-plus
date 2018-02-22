package org.clarksnut.documents.jpa;

import org.clarksnut.common.jpa.JpaModel;
import org.clarksnut.documents.DocumentProviderType;
import org.clarksnut.documents.ImportedDocumentModel;
import org.clarksnut.documents.ImportedDocumentStatus;
import org.clarksnut.documents.jpa.entity.ImportedDocumentEntity;
import org.clarksnut.files.FileModel;
import org.clarksnut.files.jpa.FileAdapter;

import javax.persistence.EntityManager;
import java.util.Date;

public class ImportedDocumentAdapter implements ImportedDocumentModel, JpaModel<ImportedDocumentEntity> {

    private final EntityManager em;
    private final ImportedDocumentEntity importedDocument;

    public ImportedDocumentAdapter(EntityManager em, ImportedDocumentEntity importedDocument) {
        this.em = em;
        this.importedDocument = importedDocument;
    }

    public static ImportedDocumentEntity toEntity(ImportedDocumentModel model, EntityManager em) {
        if (model instanceof ImportedDocumentAdapter) {
            return ((ImportedDocumentAdapter) model).getEntity();
        }
        return em.getReference(ImportedDocumentEntity.class, model.getId());
    }

    @Override
    public ImportedDocumentEntity getEntity() {
        return importedDocument;
    }

    @Override
    public String getId() {
        return importedDocument.getId();
    }

    @Override
    public DocumentProviderType getProvider() {
        return importedDocument.getProvider();
    }

    @Override
    public FileModel getFile() {
        return new FileAdapter(em, importedDocument.getFile());
    }

    @Override
    public ImportedDocumentStatus getStatus() {
        return importedDocument.getStatus();
    }

    @Override
    public void setStatus(ImportedDocumentStatus status) {
        importedDocument.setStatus(status);
    }

    @Override
    public Date getCreatedAt() {
        return importedDocument.getCreatedAt();
    }

    @Override
    public Date getUpdatedAt() {
        return importedDocument.getUpdatedAt();
    }

}
