package org.openfact.models.jpa.entity;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "USER")
@NamedQueries({
        @NamedQuery(name = "getUserByUsername", query = "select u from UserEntity u where u.username = :username")
})
public class UserEntity implements Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "ID", length = 36)
    private String id;

    @NotNull
    @NotEmpty
    @NaturalId
    @Column(name = "USERNAME")
    private String username;

    @Size(max = 255)
    @Column(name = "FULL_NAME")
    private String fullName;

    @NotNull
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "REGISTRATION_COMPLETED")
    private boolean registrationCompleted;

    @Size(max = 2048)
    @Column(name = "OFFLINE_TOKEN", length = 2048)
    private String offlineToken;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<SpaceEntity> ownedSpaces = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserSpaceEntity> memberSpaces = new HashSet<>();

    @Version
    @Column(name = "VERSION")
    private int version;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOfflineToken() {
        return offlineToken;
    }

    public void setOfflineToken(String offlineToken) {
        this.offlineToken = offlineToken;
    }

    public boolean isRegistrationCompleted() {
        return registrationCompleted;
    }

    public void setRegistrationCompleted(boolean registrationCompleted) {
        this.registrationCompleted = registrationCompleted;
    }

    public Set<UserSpaceEntity> getMemberSpaces() {
        return memberSpaces;
    }

    public void setMemberSpaces(Set<UserSpaceEntity> spaces) {
        this.memberSpaces = spaces;
    }

    public List<SpaceEntity> getOwnedSpaces() {
        return ownedSpaces;
    }

    public void setOwnedSpaces(List<SpaceEntity> ownedSpaces) {
        this.ownedSpaces = ownedSpaces;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (id != null)
            result += "id: " + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UserEntity)) {
            return false;
        }
        UserEntity other = (UserEntity) obj;
        if (id != null) {
            if (!id.equals(other.id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

}