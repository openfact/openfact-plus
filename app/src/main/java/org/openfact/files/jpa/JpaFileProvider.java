package org.openfact.files.jpa;

import org.openfact.models.FileModel;
import org.openfact.models.FileProvider;
import org.openfact.models.ModelStorageException;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Stateless
public class JpaFileProvider implements FileProvider {

    private EntityManager em;

    @Inject
    public JpaFileProvider(EntityManager em) {
        this.em = em;
    }
    
    @Override
    public FileModel addFile(byte[] file, String extension) throws ModelStorageException {
        String id = OpenfactModelUtils.generateId();

        FileEntity entity = new FileEntity();
        entity.setId(id);
        entity.setFile(file);
        entity.setFilename(id.concat(extension));
        entity.setFileExtension(extension);

        try {
            em.persist(entity);
            em.flush();
        } catch (Throwable e) {
            throw new ModelStorageException("Could not persist file", e);
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
    public boolean removeFile(FileModel file) throws ModelStorageException {
        FileEntity entity = em.find(FileEntity.class, file.getId());
        if (entity == null) return false;
        try {
            em.remove(entity);
        } catch (Throwable e) {
            throw new ModelStorageException("Could not remove file", e);
        }
        return true;
    }
}
