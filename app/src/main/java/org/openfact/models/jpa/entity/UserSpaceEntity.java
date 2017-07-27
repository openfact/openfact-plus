package org.openfact.models.jpa.entity;

import org.openfact.models.RequestStatus;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "USER_SPACE")
public class UserSpaceEntity implements Serializable {

    @EmbeddedId
    private UserSpaceIdEntity id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "SPACE_ID", insertable = false, updatable = false)
    private SpaceEntity space;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public UserSpaceEntity() {
    }

    public UserSpaceEntity(UserSpaceIdEntity id) {
        this.setId(id);
    }

    public UserSpaceIdEntity getId() {
        return id;
    }

    public void setId(UserSpaceIdEntity id) {
        this.id = id;
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

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    @Embeddable
    public static class UserSpaceIdEntity implements Serializable {

        @Column(name = "USER_ID")
        private String userId;

        @Column(name = "SPACE_ID")
        private String spaceId;

        public UserSpaceIdEntity() {
        }

        public UserSpaceIdEntity(String userId, String spaceId) {
            this.setUserId(userId);
            this.setSpaceId(spaceId);
        }

        public UserSpaceIdEntity(UserEntity user, SpaceEntity space) {
            this.setUserId(user.getId());
            this.setSpaceId(space.getId());
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getSpaceId() {
            return spaceId;
        }

        public void setSpaceId(String spaceId) {
            this.spaceId = spaceId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserSpaceIdEntity that = (UserSpaceIdEntity) o;

            if (!getUserId().equals(that.getUserId())) return false;
            return getSpaceId().equals(that.getSpaceId());
        }

        @Override
        public int hashCode() {
            int result = getUserId().hashCode();
            result = 31 * result + getSpaceId().hashCode();
            return result;
        }
    }
}
