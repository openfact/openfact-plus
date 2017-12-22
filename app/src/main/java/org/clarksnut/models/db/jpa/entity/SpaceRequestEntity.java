package org.clarksnut.models.db.jpa.entity;

import org.clarksnut.common.jpa.CreatableEntity;
import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatableEntity;
import org.clarksnut.common.jpa.UpdatedAtListener;
import org.clarksnut.documents.DocumentProviderType;
import org.clarksnut.models.RequestStatusType;
import org.clarksnut.models.RequestType;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Audited
@Entity
@Table(name = "cl_space_request", uniqueConstraints = {
        @UniqueConstraint(columnNames = "assigned_id"),
        @UniqueConstraint(columnNames = "name")
}, indexes = {
        @Index(columnList = "assigned_id", unique = true),
        @Index(columnList = "name", unique = true)
})
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
@NamedQueries({
        @NamedQuery(name = "getSpaceByAssignedId", query = "select s from SpaceEntity s where s.assignedId = :assignedId"),
        @NamedQuery(name = "getSpacesByUserId", query = "select s from SpaceEntity s inner join s.owner o where o.id = :userId")
})
public class SpaceRequestEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space", foreignKey = @ForeignKey)
    private SpaceEntity space;

    @NotNull
    @Size(max = 255)
    @Column(name = "message")
    private String message;

    @NotNull(message = "type should not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RequestType type;

    @NotNull(message = "status should not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatusType status;

    @NotNull(message = "fileId should not be null")
    @Column(name = "file_id")
    private String fileId;

    @NotNull(message = "fileProvider should not be null")
    @Column(name = "file_provider")
    private String fileProvider;

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

    public SpaceEntity getSpace() {
        return space;
    }

    public void setSpace(SpaceEntity space) {
        this.space = space;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public RequestStatusType getStatus() {
        return status;
    }

    public void setStatus(RequestStatusType status) {
        this.status = status;
    }

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
}