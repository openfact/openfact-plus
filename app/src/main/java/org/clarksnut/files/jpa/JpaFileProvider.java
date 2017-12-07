package org.clarksnut.files.jpa;

import org.clarksnut.files.FileProvider;
import org.clarksnut.files.exceptions.FileStorageException;
import org.clarksnut.files.FileModel;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Stateless
public class JpaFileProvider implements FileProvider {

    @PersistenceContext
    private EntityManager em;

    @Override
    public FileModel addFile(byte[] file, String extension) throws FileStorageException {
        String id = UUID.randomUUID().toString();

        FileEntity entity = new FileEntity();
        entity.setId(id);
        entity.setFile(file);
        entity.setFilename(id.concat(extension));
        entity.setFileExtension(extension);

        try {
            em.persist(entity);
        } catch (Throwable e) {
            throw new FileStorageException("Could not persist file", e);
        }

        return new FileAdapter(em, entity);
    }

    @Override
    public FileModel getFile(String id) {
        FileEntity entity = em.find(FileEntity.class, id);
        if (entity == null) return null;
        return new FileAdapter(em, entity);
    }

    @Override
    public boolean removeFile(FileModel file) throws FileStorageException {
        FileEntity entity = em.find(FileEntity.class, file.getId());
        if (entity == null) return false;
        try {
            em.remove(entity);
        } catch (Throwable e) {
            throw new FileStorageException("Could not remove file", e);
        }
        return true;
    }
}
