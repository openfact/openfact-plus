package org.clarksnut.files.jpa;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "cl_file")
public class FileEntity {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Column(name = "filename")
    private String filename;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file")
    private byte[] file;

    @NotNull
    @Column(name = "extension")
    private String fileExtension;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
