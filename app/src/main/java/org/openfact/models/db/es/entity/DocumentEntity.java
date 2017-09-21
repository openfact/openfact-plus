package org.openfact.models.db.es.entity;

import org.hibernate.search.annotations.Indexed;
import org.openfact.models.db.jpa.entity.SpaceEntity;
import org.openfact.models.db.jpa.entity.UserEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Entity
@Indexed
@Table(name = "UBL_DOCUMENT")
public class DocumentEntity {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "ID", length = 36)
    private String id;

    @NotNull
    @Column(name = "TYPE")
    private String type;

    @NotNull
    @Column(name = "ASSIGNED_ID")
    private String assignedId;

    @NotNull
    @Column(name = "FILE_ID")
    private String fileId;

    @ElementCollection
    @MapKeyColumn(name="NAME")
    @Column(name="VALUE")
    @CollectionTable(name="UBL_DOCUMENT_TAGS", joinColumns={ @JoinColumn(name="UBL_DOCUMENT_ID") })
    private Map<String, String> tags = new HashMap<>();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", foreignKey = @ForeignKey)
    private SpaceEntity space;

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

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public SpaceEntity getSpace() {
        return space;
    }

    public void setSpace(SpaceEntity space) {
        this.space = space;
    }
}
