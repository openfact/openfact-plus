package org.clarksnut.documents.jpa.entity;

import org.clarksnut.common.jpa.CreatableEntity;
import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatableEntity;
import org.clarksnut.common.jpa.UpdatedAtListener;
import org.clarksnut.documents.DocumentProviderType;
import org.clarksnut.documents.UnsavedReasonType;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.ContainedIn;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "cl_unsaved_document")
@NamedQueries({
        @NamedQuery(name = "getAllUnsupportedDocuments", query = "select d from DocumentEntity d")
})
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
public class UnsavedDocumentEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull(message = "fileId should not be null")
    @Column(name = "file_id")
    private String fileId;

    @NotNull(message = "fileProvider should not be null")
    @Column(name = "file_provider")
    private String fileProvider;

    @NotNull(message = "type should not be null")
    @Column(name = "type")
    private String type;

    @NotNull(message = "reason should not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private UnsavedReasonType reason;

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

    /**
     * File
     */
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileProvider() {
        return fileProvider;
    }

    public void setFileProvider(String fileProvider) {
        this.fileProvider = fileProvider;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setReason(UnsavedReasonType reason) {
        this.reason = reason;
    }

    public UnsavedReasonType getReason() {
        return reason;
    }
}

