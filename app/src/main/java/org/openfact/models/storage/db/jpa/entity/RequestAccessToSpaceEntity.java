package org.openfact.models.storage.db.jpa.entity;

import org.hibernate.validator.constraints.NotEmpty;
import org.openfact.models.PermissionType;
import org.openfact.models.RequestStatusType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "REQUEST_ACCESS")
@IdClass(RequestAccessToSpaceEntity.Key.class)
public class RequestAccessToSpaceEntity implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPACE_ID")
    private SpaceEntity space;

    @Size(max = 255)
    @Column(name = "MESSAGE")
    private String message;

    @NotNull
    @NotEmpty
    @ElementCollection(targetClass = PermissionType.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "VALUE")
    @CollectionTable(name = "REQUESTED_PERMISSIONS", joinColumns = {@JoinColumn(name = "USER_ID"), @JoinColumn(name = "SPACE_ID")})
    private Set<PermissionType> permissions = new HashSet<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private RequestStatusType status;

    @Version
    @Column(name = "VERSION")
    private int version;

    public RequestAccessToSpaceEntity() {
    }

    public RequestAccessToSpaceEntity(UserEntity user, SpaceEntity space) {
        this.user = user;
        this.space = space;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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

    public Set<PermissionType> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionType> permissions) {
        this.permissions = permissions;
    }

    public RequestStatusType getStatus() {
        return status;
    }

    public void setStatus(RequestStatusType status) {
        this.status = status;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public static class Key implements Serializable {

        private UserEntity user;
        private SpaceEntity space;

        public Key() {
        }

        public Key(UserEntity user, SpaceEntity space) {
            this.user = user;
            this.space = space;
        }

        public UserEntity getUser() {
            return user;
        }

        public SpaceEntity getSpace() {
            return space;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SharedSpaceEntity.Key key = (SharedSpaceEntity.Key) o;
            if (!getUser().equals(key.getUser())) return false;
            return getSpace().equals(key.getSpace());
        }

        @Override
        public int hashCode() {
            int result = getUser().hashCode();
            result = 31 * result + getSpace().hashCode();
            return result;
        }
    }

}