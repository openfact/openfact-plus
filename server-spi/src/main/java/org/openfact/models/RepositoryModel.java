package org.openfact.models;

import java.util.HashMap;
import java.util.Map;

public class RepositoryModel {

    private String internalId;

    /**
     * <p>An user-defined identifier to unique identify an identity provider instance.</p>
     */
    private String alias;

    /**
     * <p>An identifier used to reference a specific identity provider implementation. The value of this field is the same
     * across instances of the same provider implementation.</p>
     */
    private String repositoryId;

    private boolean enabled;

    /**
     * <p>A map containing the configuration and properties for a specific identity provider instance and implementation. The items
     * in the map are understood by the identity provider implementation.</p>
     */
    private Map<String, String> config = new HashMap<String, String>();

    public RepositoryModel() {
    }

    public RepositoryModel(RepositoryModel model) {
        if (model != null) {
            this.internalId = model.getInternalId();
            this.repositoryId = model.getRepositoryId();
            this.alias = model.getAlias();
            this.config = new HashMap<String, String>(model.getConfig());
            this.enabled = model.isEnabled();
        }
    }

    public String getInternalId() {
        return this.internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String id) {
        this.alias = id;
    }

    public String getRepositoryId() {
        return this.repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, String> getConfig() {
        return this.config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

}
