package org.openfact.models.storage.box.jpa;

import org.openfact.models.FileModel;
import org.openfact.models.StorageException;
import org.openfact.models.storage.box.jpa.entity.FileEntity;
import org.openfact.models.storage.db.jpa.JpaModel;

import javax.persistence.EntityManager;

public class FileAdapter implements FileModel, JpaModel<FileEntity> {

    private final FileEntity file;
    private final EntityManager em;

    public FileAdapter(FileEntity entity, EntityManager em) {

        this.file = entity;
        this.em = em;
    }

    public static FileEntity toEntity(FileModel model, EntityManager em) {
        if (model instanceof FileAdapter) {
            return ((FileAdapter) model).getEntity();
        }
        return em.getReference(FileEntity.class, model.getId());
    }

    @Override
    public FileEntity getEntity() {
        return file;
    }

    @Override
    public String getId() {
        return file.getFilename();
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
    public byte[] getFile() throws StorageException {
        return file.getFile();
    }

}
