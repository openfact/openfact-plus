package org.clarksnut.files.jpa;

import org.clarksnut.models.jpa.entity.ImportedDocumentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cn_file")
@NamedQueries({
        @NamedQuery(name = "removeFileByImportedDocumentId", query = "delete from FileEntity f where f in (select f from FileEntity f inner join f.importedDocuments d where d.id=:importedDocumentId)")
})
public class FileEntity implements Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Column(name = "filename")
    private String filename;

    @NotNull
    @Column(name = "checksum")
    private long checksum;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file")
    private byte[] file;

    @OneToMany(mappedBy = "file", fetch = FetchType.LAZY)
    private List<ImportedDocumentEntity> importedDocuments = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getChecksum() {
        return checksum;
    }

    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public List<ImportedDocumentEntity> getImportedDocuments() {
        return importedDocuments;
    }

    public void setImportedDocuments(List<ImportedDocumentEntity> importedDocuments) {
        this.importedDocuments = importedDocuments;
    }
}
