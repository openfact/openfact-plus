package org.openfact.models.jpa.entity;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
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
    private Set<SpaceEntity> ownedSpaces = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<SharedSpaceEntity> sharedSpaces = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<RequestAccessToSpaceEntity> spaceRequests = new HashSet<>();

    @Version
    @Column(name = "VERSION")
    private int version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public boolean isRegistrationCompleted() {
        return registrationCompleted;
    }

    public void setRegistrationCompleted(boolean registrationCompleted) {
        this.registrationCompleted = registrationCompleted;
    }

    public String getOfflineToken() {
        return offlineToken;
    }

    public void setOfflineToken(String offlineToken) {
        this.offlineToken = offlineToken;
    }

    public Set<SpaceEntity> getOwnedSpaces() {
        return ownedSpaces;
    }

    public void setOwnedSpaces(Set<SpaceEntity> ownedSpaces) {
        this.ownedSpaces = ownedSpaces;
    }

    public Set<SharedSpaceEntity> getSharedSpaces() {
        return sharedSpaces;
    }

    public void setSharedSpaces(Set<SharedSpaceEntity> sharedSpaces) {
        this.sharedSpaces = sharedSpaces;
    }

    public Set<RequestAccessToSpaceEntity> getSpaceRequests() {
        return spaceRequests;
    }

    public void setSpaceRequests(Set<RequestAccessToSpaceEntity> spaceRequests) {
        this.spaceRequests = spaceRequests;
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
        UserEntity that = (UserEntity) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

}
