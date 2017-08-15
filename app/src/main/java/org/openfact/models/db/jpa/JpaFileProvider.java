package org.openfact.models.db.jpa;

import org.openfact.models.FileModel;
import org.openfact.models.FileProvider;
import org.openfact.models.ModelException;
import org.openfact.models.StorageException;
import org.openfact.models.db.HibernateProvider;
import org.openfact.models.db.jpa.entity.FileEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Stateless
public class JpaFileProvider extends HibernateProvider implements FileProvider {

    private EntityManager em;

    @Inject
    public JpaFileProvider(EntityManager em) {
        this.em = em;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public FileModel addFile(byte[] file, String extension) throws StorageException {
        String id = OpenfactModelUtils.generateId();

        FileEntity entity = new FileEntity();
        entity.setId(id);
        entity.setFile(file);
        entity.setFilename(id.concat(extension));
        entity.setFileExtension(extension);

        try {
            getSession().persist(entity);
            getSession().flush();
        } catch (Throwable e) {
            throw new StorageException("Could not persist file", e);
        }

        return new FileAdapter(getSession(), entity);
    }

    @Override
    public FileModel getFile(String id) {
        FileEntity entity = getSession().find(FileEntity.class, id);
        if (entity == null) return null;
        return new FileAdapter(getSession(), entity);
    }

    @Override
    public boolean removeFile(FileModel file) throws StorageException {
        FileEntity entity = getSession().find(FileEntity.class, file.getId());
        if (entity == null) return false;
        try {
            getSession().remove(entity);
        } catch (Throwable e) {
            throw new StorageException("Could not remove file", e);
        }
        return true;
    }
}
