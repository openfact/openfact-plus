package org.openfact.models.jpa;

import org.openfact.models.RepositoryModel;
import org.openfact.models.RepositoryProvider;
import org.openfact.models.jpa.entities.RepositoryEntity;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
public class JpaRepositoryProvider implements RepositoryProvider {

    @Inject
    private EntityManager em;

    private RepositoryModel entityToModel(RepositoryEntity entity) {
        RepositoryModel repositoryModel = new RepositoryModel();
        repositoryModel.setRepositoryId(entity.getRepositoryId());
        repositoryModel.setAlias(entity.getAlias());

        repositoryModel.setInternalId(entity.getInternalId());
        Map<String, String> config = entity.getConfig();
        Map<String, String> copy = new HashMap<>();
        copy.putAll(config);
        repositoryModel.setConfig(copy);
        repositoryModel.setEnabled(entity.isEnabled());
        return repositoryModel;
    }

    @Override
    public void addRepository(RepositoryModel repository) {
        RepositoryEntity entity = new RepositoryEntity();

        entity.setInternalId(UUID.randomUUID().toString());
        entity.setAlias(repository.getAlias());
        entity.setRepositoryId(repository.getRepositoryId());
        entity.setEnabled(repository.isEnabled());
        entity.setConfig(repository.getConfig());

        repository.setInternalId(entity.getInternalId());

        em.persist(entity);
        em.flush();
    }

    @Override
    public RepositoryModel getRepositoryByAlias(String alias) {
        TypedQuery<RepositoryEntity> query = em.createNamedQuery("findRepositoryByAlias", RepositoryEntity.class);
        query.setParameter("alias", alias);
        List<RepositoryEntity> entities = query.getResultList();
        if (entities.size() == 0) return null;
        return entityToModel(entities.get(0));
    }

    @Override
    public List<RepositoryModel> getRepositories() {
        TypedQuery<RepositoryEntity> query = em.createNamedQuery("getAllRepositories", RepositoryEntity.class);
        List<RepositoryEntity> entities = query.getResultList();
        return entities.stream().map(this::entityToModel).collect(Collectors.toList());
    }

    @Override
    public boolean removeRepositoryByAlias(String alias) {
        TypedQuery<RepositoryEntity> query = em.createNamedQuery("findRepositoryByAlias", RepositoryEntity.class);
        query.setParameter("alias", alias);
        List<RepositoryEntity> entities = query.getResultList();
        if (entities.size() == 0) return false;
        em.remove(entities.get(0));
        em.flush();
        return true;
    }

    @Override
    public void updateRepository(RepositoryModel repository) {
        RepositoryEntity entity = em.find(RepositoryEntity.class, repository.getInternalId());
        if (entity != null) {
            entity.setAlias(repository.getAlias());
            entity.setEnabled(repository.isEnabled());
            entity.setConfig(repository.getConfig());
        }
        em.merge(entity);
        em.flush();
    }

}
