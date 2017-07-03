package org.openfact.models;

import java.util.List;

public interface RepositoryProvider {

    void addRepository(RepositoryModel repository);

    RepositoryModel getRepositoryByAlias(String alias);

    List<RepositoryModel> getRepositories();

    boolean removeRepositoryByAlias(String alias);

    void updateRepository(RepositoryModel repository);

}
