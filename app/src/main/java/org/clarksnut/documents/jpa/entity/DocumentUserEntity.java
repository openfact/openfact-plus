package org.clarksnut.documents.jpa.entity;

import org.clarksnut.common.jpa.CreatableEntity;
import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatableEntity;
import org.clarksnut.common.jpa.UpdatedAtListener;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cl_document_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "document_id"})
})
@NamedQueries({
        @NamedQuery(name = "getDocumentUserByUserAndDocument", query = "select du from DocumentUserEntity du inner join du.document doc where doc.id = :documentId and du.userId = :userId"),
        @NamedQuery(name = "getDocumentUserByUserAndTypeAssignedIdAndSupplierAssignedId", query = "select du from DocumentUserEntity du inner join du.document doc where doc.type = :type and doc.assignedId = :assignedId and doc.supplierAssignedId = :supplierAssignedId and du.userId = :userId")
})
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
public class DocumentUserEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "starred")
    private boolean starred;

    @NotNull
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "viewed")
    private boolean viewed;

    @NotNull
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "checked")
    private boolean checked;

    @NotNull
    @Column(name = "user_id")
    private String userId;

    @ElementCollection
    @Column(name = "value")
    @CollectionTable(name = "document_tags", joinColumns = {@JoinColumn(name = "document_id")})
    private Set<String> tags = new HashSet<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", foreignKey = @ForeignKey)
    private DocumentEntity document;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
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

