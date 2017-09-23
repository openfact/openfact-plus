package org.openfact.repositories.managers;

import org.openfact.models.UserRepositoryModel;

public interface MailSynchronizer {
    void synchronize(UserRepositoryModel repository);
}
