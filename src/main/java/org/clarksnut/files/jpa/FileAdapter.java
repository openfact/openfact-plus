package org.clarksnut.files.jpa;

import org.clarksnut.files.FileModel;
import org.clarksnut.files.exceptions.FileFetchException;

import javax.persistence.EntityManager;

public class FileAdapter implements FileModel {

    private final FileEntity file;
    private final EntityManager em;

    public FileAdapter(EntityManager em, FileEntity entity) {
        this.file = entity;
        this.em = em;
    }

    @Override
    public String getId() {
        return file.getId();
    }

    @Override
    public String getFilename() {
        return file.getFilename();
    }

    @Override
    public String getExtension() {
        return file.getFileExtension();
    }

    @Override
    public byte[] getFile() throws FileFetchException {
        return file.getFile();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FileModel)) {
            return false;
        }
        FileModel other = (FileModel) obj;
        if (getId() != null) {
            if (!getId().equals(other.getId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

}
