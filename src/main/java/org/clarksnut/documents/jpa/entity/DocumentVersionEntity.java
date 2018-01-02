package org.clarksnut.documents.jpa.entity;

import org.clarksnut.common.jpa.CreatableEntity;
import org.clarksnut.common.jpa.CreatedAtListener;
import org.clarksnut.common.jpa.UpdatableEntity;
import org.clarksnut.common.jpa.UpdatedAtListener;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "cl_document_version")
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
public class DocumentVersionEntity extends AbstractDocumentEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull(message = "isCurrentVersion should not be null")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "is_current_version")
    private boolean isCurrentVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", foreignKey = @ForeignKey)
    private DocumentEntity document;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCurrentVersion() {
        return isCurrentVersion;
    }

    public void setCurrentVersion(boolean isCurrentVersion) {
        this.isCurrentVersion = isCurrentVersion;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

}

