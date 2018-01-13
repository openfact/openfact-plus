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
import java.util.Set;
import java.util.stream.Collectors;

public class ImportedDocumentAdapter implements ImportedDocumentModel, JpaModel<ImportedDocumentEntity> {

    private final EntityManager em;
    private final ImportedDocumentEntity document;

    public ImportedDocumentAdapter(EntityManager em, ImportedDocumentEntity document) {
        this.em = em;
        this.document = document;
    }

    public static ImportedDocumentEntity toEntity(ImportedDocumentModel model, EntityManager em) {
        if (model instanceof ImportedDocumentAdapter) {
            return ((ImportedDocumentAdapter) model).getEntity();
        }
        return em.getReference(ImportedDocumentEntity.class, model.getId());
    }

    @Override
    public ImportedDocumentEntity getEntity() {
        return document;
    }

    @Override
    public String getId() {
        return document.getId();
    }

    @Override
    public DocumentProviderType getProvider() {
        return document.getProvider();
    }

    @Override
    public boolean isCompressed() {
        return document.isCompressed();
    }

    @Override
    public FileModel getFile() {
        return new FileAdapter(em, document.getFile());
    }

    @Override
    public ImportedDocumentStatus getStatus() {
        return document.getStatus();
    }

    @Override
    public void setStatus(ImportedDocumentStatus status) {
        document.setStatus(status);
    }

    @Override
    public Date getCreatedAt() {
        return document.getCreatedAt();
    }

    @Override
    public Date getUpdatedAt() {
        return document.getUpdatedAt();
    }

    @Override
    public ImportedDocumentModel getParent() {
        ImportedDocumentEntity parent = document.getParent();
        if (parent != null) {
            return new ImportedDocumentAdapter(em, parent);
        }
        return null;
    }

    @Override
    public Set<ImportedDocumentModel> getChildren() {
        return document.getChildren().stream()
                .map(f -> new ImportedDocumentAdapter(em, f))
                .collect(Collectors.toSet());
    }

    @Override
    public String getDocumentReferenceId(String id) {
        return document.getDocumentReferenceId();
    }

    @Override
    public void setDocumentReferenceId(String documentReferenceId) {
        document.setDocumentReferenceId(documentReferenceId);
    }

}
