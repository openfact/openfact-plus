package org.openfact.models.storage.box.jpa.entity;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "FILE")
public class FileEntity {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "FILENAME", length = 36)
    private String filename;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "FILE")
    private byte[] file;

    @NotEmpty
    @NotNull
    @Column(name = "EXTENSION")
    private String fileExtension;

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return fileExtension;
    }

}
