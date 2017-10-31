package org.openfact.models.db.jpa.entity;

import org.hibernate.envers.Audited;
import org.openfact.models.db.CreatableEntity;
import org.openfact.models.db.CreatedAtListener;
import org.openfact.models.db.UpdatableEntity;
import org.openfact.models.db.UpdatedAtListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Audited
@Entity
@Table(name = "space", uniqueConstraints = {
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
public class SpaceEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Column(name = "assigned_id")
    private String assignedId;

    @NotNull
    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey)
    private UserEntity owner;

    @OneToMany(mappedBy = "space", fetch = FetchType.LAZY)
    private Set<CollaboratorEntity> collaborators = new HashSet<>();
//
//    @OneToMany(mappedBy = "space", fetch = FetchType.LAZY)
//    private Set<RequestAccessToSpaceEntity> accessRequests = new HashSet<>();

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @Version
    @Column(name = "version")
    private int version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String alias) {
        this.name = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public Set<CollaboratorEntity> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(Set<CollaboratorEntity> collaborators) {
        this.collaborators = collaborators;
    }

//    public Set<RequestAccessToSpaceEntity> getAccessRequests() {
//        return accessRequests;
//    }
//
//    public void setAccessRequests(Set<RequestAccessToSpaceEntity> accessRequests) {
//        this.accessRequests = accessRequests;
//    }

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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpaceEntity that = (SpaceEntity) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

}