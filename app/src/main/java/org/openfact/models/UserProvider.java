package org.openfact.models;

public interface UserProvider {

    UserModel addUser(String username);

    UserModel getByUsername(String username);

    ScrollableResultsModel<UserModel> getUsers();

}
