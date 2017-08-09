package org.openfact.repositories.user;

import org.openfact.models.UserRepositoryModel;

import java.util.List;

public interface UserRepositoryReader {
    List<UserRepositoryElementModel> read(UserRepositoryModel repository, UserRepositoryQuery query) throws UserRepositoryReadException;
}
