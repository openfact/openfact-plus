package org.clarksnut.models.jpa.entity;

import org.clarksnut.models.PermissionType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "cn_collaborator", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "space_id"})
})
@IdClass(CollaboratorEntity.Key.class)
@NamedQueries({
        @NamedQuery(name = "getCollaboratorsBySpaceId", query = "select c from CollaboratorEntity c inner join c.space s where s.id = :spaceId"),
        @NamedQuery(name = "getCollaboratorsBySpaceIdAndRole", query = "select c from CollaboratorEntity c inner join c.space s where s.id = :spaceId and c.role =:role"),
        @NamedQuery(name = "getCollaboratorsByUserId", query = "select c from CollaboratorEntity c inner join c.user u where u.id = :userId"),
        @NamedQuery(name = "getCollaboratorsByUserIdAndRole", query = "select c from CollaboratorEntity c inner join c.user u where u.id = :userId and c.role in :role"),
        @NamedQuery(name = "countCollaboratorsByUserIdAndRole", query = "select count(c) from CollaboratorEntity c inner join c.user u where u.id = :userId and c.role in :role"),
        @NamedQuery(name = "deleteCollaboratorsBySpaceId", query = "delete from CollaboratorEntity c where c.space.id = :spaceId"),
        @NamedQuery(name = "deleteCollaboratorsBySpaceIdAndRole", query = "delete from CollaboratorEntity c where c.space.id = :spaceId and c.role =:role"),
})
public class CollaboratorEntity implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private SpaceEntity space;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private PermissionType role;

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

    public PermissionType getRole() {
        return role;
    }

    public void setRole(PermissionType role) {
        this.role = role;
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
