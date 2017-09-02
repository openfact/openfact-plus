package org.openfact.models.db.jpa.entity;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
@NamedQueries({
        @NamedQuery(name = "getAllUsers", query = "select u from UserEntity u order by u.username"),
        @NamedQuery(name = "getUserByUsername", query = "select u from UserEntity u where u.username = :username"),
        @NamedQuery(name = "getUserByIdentityID", query = "select u from UserEntity u where u.identityID = :identityID")
})
public class UserEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @NotEmpty
    @Column(name = "identity_id")
    private String identityID;

    @NotNull
    @NotEmpty
    @Column(name = "provider_type")
    private String providerType;

    @NotEmpty
    @Column(name = "username")
    private String username;

    @Size(max = 255)
    @Column(name = "full_name")
    private String fullName;

    @Size(max = 255)
    @Column(name = "image_url")
    private String imageURL;

    @Size(max = 255)
    @Column(name = "bio")
    private String bio;

    @Email
    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 255)
    @Column(name = "company")
    private String company;

    @Size(max = 255)
    @Column(name = "url")
    private String url;

    @NotNull
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @NotNull
    @Type(type = "org.hibernate.type.TrueFalseType")
    @Column(name = "registration_complete")
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

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserRepositoryEntity> repositories = new HashSet<>();

    @Version
    @Column(name = "version")
    private int version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentityID() {
        return identityID;
    }

    public void setIdentityID(String identityID) {
        this.identityID = identityID;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public Set<UserRepositoryEntity> getRepositories() {
        return repositories;
    }

    public void setRepositories(Set<UserRepositoryEntity> repositories) {
        this.repositories = repositories;
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
