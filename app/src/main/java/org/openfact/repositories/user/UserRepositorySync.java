package org.openfact.repositories.user;

import org.openfact.models.UserRepositoryModel;

public interface UserRepositorySync {
    void synchronize(UserRepositoryModel repository);
}
