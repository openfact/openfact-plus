package org.openfact.models.jpa.entity;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SPACE")
@NamedQueries({
        @NamedQuery(name = "getSpaceByAccountId", query = "select s from SpaceEntity s where s.accountId = :accountId")
})
public class SpaceEntity implements Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "ID", length = 36)
    private String id;

    @NotNull
    @NaturalId
    @Column(name = "ACCOUNT_ID")
    private String accountId;

    @Size(max = 255)
    @Column(name = "ALIAS")
    private String alias;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", foreignKey = @ForeignKey)
    private UserEntity owner;

    @OneToMany(mappedBy = "space", fetch = FetchType.LAZY)
    private Set<UserSpaceEntity> members = new HashSet<>();

    @Version
    @Column(name = "VERSION")
    private int version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String assignedAccountId) {
        this.accountId = assignedAccountId;
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

    public Set<UserSpaceEntity> getMembers() {
        return members;
    }

    public void setMembers(Set<UserSpaceEntity> members) {
        this.members = members;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (getId() != null)
            result += "id: " + getId();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SpaceEntity)) {
            return false;
        }
        SpaceEntity other = (SpaceEntity) obj;
        if (getId() != null) {
            if (!getId().equals(other.getId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

}