package org.clarksnut.files.jpa;

import org.clarksnut.files.FileModel;
import org.clarksnut.files.FileProvider;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Stateless
public class JpaFileProvider implements FileProvider {

    @PersistenceContext
    private EntityManager em;

    @Override
    public FileModel addFile(String filename, byte[] file)  {
        String id = UUID.randomUUID().toString();

        FileEntity entity = new FileEntity();
        entity.setId(id);
        entity.setFilename(filename);
        entity.setFile(file);

        em.persist(entity);
        return new FileAdapter(em, entity);
    }

    @Override
    public FileModel getFile(String id) {
        FileEntity entity = em.find(FileEntity.class, id);
        if (entity == null) return null;
        return new FileAdapter(em, entity);
    }

    @Override
    public boolean removeFile(FileModel file)  {
        FileEntity entity = em.find(FileEntity.class, file.getId());
        if (entity == null) return false;
        em.remove(entity);
        return true;
    }
}
