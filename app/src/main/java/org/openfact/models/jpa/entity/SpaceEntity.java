package org.openfact.models.jpa.entity;

import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SPACE")
@NamedQueries({
        @NamedQuery(name = "getSpaceByAssignedId", query = "select s from SpaceEntity s where s.assignedId = :assignedId")
})
public class SpaceEntity implements Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "ID", length = 36)
    private String id;

    @NotNull
    @NotEmpty
    @NaturalId
    @Column(name = "ASSIGNED_ID")
    private String assignedId;

    @Size(max = 255)
    @Column(name = "ALIAS")
    private String alias;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", foreignKey = @ForeignKey)
    private UserEntity owner;

    @OneToMany(mappedBy = "space", fetch = FetchType.LAZY)
    private Set<SharedSpaceEntity> sharedUsers = new HashSet<>();

    @OneToMany(mappedBy = "space", fetch = FetchType.LAZY)
    private Set<RequestAccessToSpaceEntity> accessRequests = new HashSet<>();

    @Version
    @Column(name = "VERSION")
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public Set<SharedSpaceEntity> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(Set<SharedSpaceEntity> sharedUsers) {
        this.sharedUsers = sharedUsers;
    }

    public Set<RequestAccessToSpaceEntity> getAccessRequests() {
        return accessRequests;
    }

    public void setAccessRequests(Set<RequestAccessToSpaceEntity> accessRequests) {
        this.accessRequests = accessRequests;
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