package org.clarksnut.models.jpa.entity;

import org.clarksnut.common.jpa.CreatableEntity;
import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatableEntity;
import org.clarksnut.common.jpa.UpdatedAtListener;
import org.clarksnut.files.jpa.FileEntity;
import org.clarksnut.models.DocumentProviderType;
import org.clarksnut.models.ImportedDocumentStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Cacheable
@Entity
@Table(name = "cn_imported_document")
@NamedQueries({
        @NamedQuery(name = "removeImportedDocumentById", query = "delete from ImportedDocumentEntity i where i.id =:importedDocumentId")
})
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
public class ImportedDocumentEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ImportedDocumentStatus status;

    @NotNull(message = "provider should not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private DocumentProviderType provider;

    @OneToOne(mappedBy = "importedDocument", fetch = FetchType.LAZY)
    private DocumentVersionEntity documentVersion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", foreignKey = @ForeignKey)
    private FileEntity file;

    @NotNull(message = "createdAt should not be null")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @NotNull(message = "updatedAt should not be null")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ImportedDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(ImportedDocumentStatus status) {
        this.status = status;
    }

    public DocumentProviderType getProvider() {
        return provider;
    }

    public void setProvider(DocumentProviderType provider) {
        this.provider = provider;
    }

    public DocumentVersionEntity getDocumentVersion() {
        return documentVersion;
    }

    public void setDocumentVersion(DocumentVersionEntity documentVersion) {
        this.documentVersion = documentVersion;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

