package org.openfact.models.jpa.entity;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;

@Entity
@Table
@NamedQueries({
        @NamedQuery(name = "getUserByUsername", query = "select u from UserEntity u where u.username = :username")
})
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private String id;

    @NaturalId
    @NotNull
    @Column(name = "USERNAME")
    private String username;

    @Column(name = "OFFLINE_TOKEN", length = 2048)
    private String offlineToken;

    @Column(name = "REGISTRATION_COMPLETED")
    private boolean registrationCompleted;

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