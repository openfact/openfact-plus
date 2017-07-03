package org.openfact.models.jpa.entities;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "REPOSITORY", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"REPOSITORY_ALIAS"})
})
@NamedQueries({
        @NamedQuery(name = "getAllRepositories", query = "select repository from RepositoryEntity repository"),
        @NamedQuery(name = "findRepositoryByAlias", query = "select repository from RepositoryEntity repository where repository.alias = :alias")
})
public class RepositoryEntity {

    @Id
    @Column(name = "INTERNAL_ID", length = 36)
    @Access(AccessType.PROPERTY)
    // we do this because relationships often fetch id, but not entity.  This avoids an extra SQL
    private String internalId;

    @Column(name = "REPOSITORY_ID")
    private String repositoryId;

    @Column(name = "REPOSITORY_ALIAS")
    private String alias;

    @Column(name = "ENABLED")
    private boolean enabled;

    @ElementCollection
    @MapKeyColumn(name = "NAME")
    @Column(name = "VALUE", columnDefinition = "TEXT")
    @CollectionTable(name = "REPOSITORY_CONFIG", joinColumns = {@JoinColumn(name = "REPOSITORY_ID")})
    private Map<String, String> config;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof RepositoryEntity)) return false;

        RepositoryEntity that = (RepositoryEntity) o;

        if (!getInternalId().equals(that.getInternalId())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getInternalId().hashCode();
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }
}