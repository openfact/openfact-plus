package org.openfact.models.storage.jpa;

import org.openfact.models.FileModel;
import org.openfact.models.FileProvider;
import org.openfact.models.StorageException;
import org.openfact.models.storage.jpa.entity.FileEntity;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.UUID;

@Stateless
public class JpaFileProvider implements FileProvider {

    @Inject
    private EntityManager em;

    @Override
    public FileModel addFile(byte[] file, String extension) throws StorageException {
        FileEntity entity = new FileEntity();
        entity.setFilename(OpenfactModelUtils.generateId());
        entity.setFileExtension(extension);
        entity.setFile(file);
        em.persist(entity);
        return new FileAdapter(entity, em);
    }

    @Override
    public boolean removeFile(FileModel file) throws StorageException {
        FileAdapter.toEntity(file, em);
        return false;
    }

}
