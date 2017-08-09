package org.openfact.models.storage.db.jpa.entity;

import org.hibernate.validator.constraints.NotEmpty;
import org.openfact.models.PermissionType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SHARED_SPACE")
@IdClass(SharedSpaceEntity.Key.class)
public class SharedSpaceEntity implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPACE_ID")
    private SpaceEntity space;

    @NotNull
    @NotEmpty
    @ElementCollection(targetClass = PermissionType.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "VALUE")
    @CollectionTable(name = "PERMISSIONS", joinColumns = {@JoinColumn(name = "USER_ID"), @JoinColumn(name = "SPACE_ID")})
    private Set<PermissionType> permissions = new HashSet<>();

    public SharedSpaceEntity() {
    }

    public SharedSpaceEntity(UserEntity user, SpaceEntity space) {
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

    public Set<PermissionType> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionType> permissions) {
        this.permissions = permissions;
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
            Key key = (Key) o;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedSpaceEntity that = (SharedSpaceEntity) o;
        if (!getUser().equals(that.getUser())) return false;
        return getSpace().equals(that.getSpace());
    }

    @Override
    public int hashCode() {
        int result = getUser().hashCode();
        result = 31 * result + getSpace().hashCode();
        return result;
    }

}
