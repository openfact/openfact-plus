package org.openfact.models;

public interface UserProvider {

    UserModel getByUsername(String username);

    UserModel addUser(String username);

}
