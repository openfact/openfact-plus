package org.clarksnut.models.jpa.entity;

import org.clarksnut.common.jpa.CreatableEntity;
import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatableEntity;
import org.clarksnut.common.jpa.UpdatedAtListener;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Cacheable
@Entity
@Table(name = "cn_party", uniqueConstraints = {
        @UniqueConstraint(columnNames = "assignedId")
})
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
@NamedQueries({
        @NamedQuery(name = "getIndexedPartyById", query = "select d from PartyEntity d where d.id=:partyId"),
        @NamedQuery(name = "getIndexedPartyByAssignedId", query = "select d from PartyEntity d where d.assignedId=:assignedId"),
        @NamedQuery(name = "getIndexedPartyByAssignedIdAndSpaceId", query = "select distinct p from PartyEntity p join p.spaceIds s where p.assignedId=:assignedId and s=:spaceAssignedId")
})
public class PartyEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NaturalId
    @NotNull(message = "assignedId should not be null")
    @Column(name = "assignedId")
    private String assignedId;

    @NotNull(message = "assignedId should not be null")
    @Column(name = "name")
    private String name;

    @ElementCollection
    @Column(name = "value")
    @CollectionTable(name = "party_names", joinColumns = {@JoinColumn(name = "party_id")})
    private Set<String> names = new HashSet<>();

    @ElementCollection
    @Column(name = "value")
    @CollectionTable(name = "party_space_ids", joinColumns = {@JoinColumn(name = "party_id")})
    private Set<String> spaceIds = new HashSet<>();

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

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }

    public Set<String> getSpaceIds() {
        return spaceIds;
    }

    public void setSpaceIds(Set<String> spaceIds) {
        this.spaceIds = spaceIds;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
