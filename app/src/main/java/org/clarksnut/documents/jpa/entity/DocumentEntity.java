package org.clarksnut.documents.jpa.entity;

import org.clarksnut.common.jpa.CreatableEntity;
import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatableEntity;
import org.clarksnut.common.jpa.UpdatedAtListener;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "cl_document", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type", "assigned_id", "supplier_assigned_id"})
})
@NamedQueries({
        @NamedQuery(name = "getDocumentByTypeAssignedIdAndSupplierAssignedId", query = "select d from DocumentEntity d where d.type = :type and d.assignedId = :assignedId and d.supplierAssignedId = :supplierAssignedId")
})
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
public class DocumentEntity extends AbstractDocumentEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull(message = "changed should not be null")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "changed")
    private boolean changed;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private Set<DocumentUserEntity> documentUsers;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<DocumentVersionEntity> versions = new ArrayList<>();

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public Set<DocumentUserEntity> getDocumentUsers() {
        return documentUsers;
    }

    public void setDocumentUsers(Set<DocumentUserEntity> documentUsers) {
        this.documentUsers = documentUsers;
    }

    public List<DocumentVersionEntity> getVersions() {
        return versions;
    }

    public void setVersions(List<DocumentVersionEntity> versions) {
        this.versions = versions;
    }
}

