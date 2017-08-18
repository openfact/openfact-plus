package org.openfact.models;

import java.util.List;

public interface UserProvider {

    UserModel addUser(String username);

    UserModel getUser(String userId);

    UserModel getByUsername(String username);

    List<UserModel> getUsers();

    ScrollableResultsModel<UserModel> getScrollableUsers();

}
