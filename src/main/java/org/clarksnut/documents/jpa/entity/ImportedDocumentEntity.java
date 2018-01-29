package org.clarksnut.documents.jpa.entity;

import org.clarksnut.common.jpa.CreatableEntity;
import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatableEntity;
import org.clarksnut.common.jpa.UpdatedAtListener;
import org.clarksnut.documents.DocumentProviderType;
import org.clarksnut.documents.ImportedDocumentStatus;
import org.clarksnut.files.jpa.FileEntity;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cl_imported_document")
@NamedQueries({
        @NamedQuery(name = "getAllUnsupportedDocuments", query = "select d from DocumentEntity d")
})
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
public class ImportedDocumentEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "compressed")
    private boolean compressed;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ImportedDocumentStatus status;

    @NotNull(message = "provider should not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private DocumentProviderType provider;

    @NotNull(message = "documentReferenceId should not be null")
    @Column(name = "document_reference_id")
    private String documentReferenceId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", foreignKey = @ForeignKey)
    private FileEntity file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey)
    private ImportedDocumentEntity parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<ImportedDocumentEntity> children = new ArrayList<>();

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

    public DocumentProviderType getProvider() {
        return provider;
    }

    public void setProvider(DocumentProviderType provider) {
        this.provider = provider;
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

    public void setParent(ImportedDocumentEntity parent) {
        this.parent = parent;
    }

    public ImportedDocumentEntity getParent() {
        return parent;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public ImportedDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(ImportedDocumentStatus status) {
        this.status = status;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }

    public List<ImportedDocumentEntity> getChildren() {
        return children;
    }

    public void setChildren(List<ImportedDocumentEntity> children) {
        this.children = children;
    }

    public String getDocumentReferenceId() {
        return documentReferenceId;
    }

    public void setDocumentReferenceId(String documentReferenceId) {
        this.documentReferenceId = documentReferenceId;
    }
}

