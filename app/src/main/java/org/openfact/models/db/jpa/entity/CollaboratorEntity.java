package org.openfact.models.db.jpa.entity;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Audited
@Entity
@Table(name = "collaborator")
@IdClass(CollaboratorEntity.Key.class)
@NamedQueries({
        @NamedQuery(name = "getCollaboratorsBySpaceId", query = "select c from CollaboratorEntity c inner join c.space s where s.id = :spaceId")
})
public class CollaboratorEntity implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPACE_ID")
    private SpaceEntity space;

//    @NotNull
//    @ElementCollection(targetClass = PermissionType.class)
//    @Enumerated(EnumType.STRING)
//    @Column(name = "VALUE")
//    @CollectionTable(name = "PERMISSIONS", joinColumns = {@JoinColumn(name = "USER_ID"), @JoinColumn(name = "SPACE_ID")})
//    private Set<PermissionType> permissions = new HashSet<>();

    public CollaboratorEntity() {
    }

    public CollaboratorEntity(UserEntity user, SpaceEntity space) {
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

//    public Set<PermissionType> getPermissions() {
//        return permissions;
//    }
//
//    public void setPermissions(Set<PermissionType> permissions) {
//        this.permissions = permissions;
//    }

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
        CollaboratorEntity that = (CollaboratorEntity) o;
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
