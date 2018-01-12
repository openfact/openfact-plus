package org.clarksnut.documents.jpa.entity;

import org.clarksnut.common.jpa.CreatableEntity;
import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatableEntity;
import org.clarksnut.common.jpa.UpdatedAtListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cl_document", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type", "assigned_id", "supplier_assigned_id"})
})
@NamedQueries({
        @NamedQuery(name = "getAllDocuments", query = "select d from DocumentEntity d"),
        @NamedQuery(name = "getDocumentByTypeAssignedIdAndSupplierAssignedId", query = "select d from DocumentEntity d where d.type = :type and d.assignedId = :assignedId and d.supplierAssignedId = :supplierAssignedId")
})
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
public class DocumentEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull(message = "type should not be null")
    @Column(name = "type")
    private String type;

    @NotNull(message = "supplierAssignedId should not be null")
    @Column(name = "supplier_assigned_id")
    private String supplierAssignedId;

    @NotNull(message = "assignedId should not be null")
    @Column(name = "assigned_id")
    private String assignedId;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    private List<DocumentVersionEntity> versions = new ArrayList<>();

    @NotNull(message = "createdAt should not be null")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @NotNull(message = "updatedAt should not be null")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSupplierAssignedId() {
        return supplierAssignedId;
    }

    public void setSupplierAssignedId(String supplierAssignedId) {
        this.supplierAssignedId = supplierAssignedId;
    }

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    public List<DocumentVersionEntity> getVersions() {
        return versions;
    }

    public void setVersions(List<DocumentVersionEntity> versions) {
        this.versions = versions;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}

