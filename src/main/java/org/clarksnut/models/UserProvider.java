package org.clarksnut.models;

import java.util.List;

public interface UserProvider {

    /**
     * @param providerType provider of identityID
     * @param username     username of the user
     * @return user created
     */
    UserModel addUser(String username, String providerType, String identityId);

    /**
     * @param id auto generated unique identity
     * @return user, in case is not found return null
     */
    UserModel getUser(String id);

    /**
     * @param username username of the user
     * @return user, in case is not found return null
     */
    UserModel getUserByUsername(String username);

    /**
     * @param query query to apply on search operation
     * @return all users after apply query
     */
    @Deprecated
    List<UserModel> getUsers(QueryModel query);

    List<UserModel> getUsers(String filterText);

    List<UserModel> getUsers(String filterText, int offset, int limit);
}
