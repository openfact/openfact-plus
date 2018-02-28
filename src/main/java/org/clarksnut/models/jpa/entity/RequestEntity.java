package org.clarksnut.models.jpa.entity;

import org.clarksnut.common.jpa.CreatableEntity;
import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatableEntity;
import org.clarksnut.common.jpa.UpdatedAtListener;
import org.clarksnut.models.PermissionType;
import org.clarksnut.models.RequestStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "cl_request")
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
@NamedQueries({
        @NamedQuery(name = "getRequestsByStatusAndSpaces", query = "select r from RequestEntity r inner join r.space s where r.status =:status and r.space in :spaces")
})
public class RequestEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Column(name = "message")
    private String message;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    private PermissionType permission;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", foreignKey = @ForeignKey)
    private SpaceEntity space;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey)
    private UserEntity user;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public PermissionType getPermission() {
        return permission;
    }

    public void setPermission(PermissionType scope) {
        this.permission = scope;
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

    public void setSpace(SpaceEntity space) {
        this.space = space;
    }

    public SpaceEntity getSpace() {
        return space;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}