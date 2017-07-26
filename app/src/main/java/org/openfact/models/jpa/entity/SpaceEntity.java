package org.openfact.models.jpa.entity;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SPACE")
@NamedQueries({
        @NamedQuery(name = "getUserByUsername", query = "select u from UserEntity u where u.username = :username")
})
public class SpaceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private String id;

    @NaturalId
    @NotNull
    @Column(name = "ASSIGNED_ACCOUNT_ID")
    private String assignedAccountId;

    @NotNull
    @Column(name = "ALIAS")
    private String alias;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", foreignKey = @ForeignKey)
    private UserEntity owner;

    @Version
    @Column(name = "VERSION")
    private int version;

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
        if (!(obj instanceof SpaceEntity)) {
            return false;
        }
        SpaceEntity other = (SpaceEntity) obj;
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