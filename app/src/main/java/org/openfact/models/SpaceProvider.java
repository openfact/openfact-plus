package org.openfact.models;

public interface SpaceProvider {

    SpaceModel addSpace(String accountId, UserModel owner);

    SpaceModel getByAccountId(String accountId);

}
