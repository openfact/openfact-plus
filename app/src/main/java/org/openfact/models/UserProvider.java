package org.openfact.models;

import java.util.List;

public interface UserProvider {

    /**
     * @param identityID   a unique identity string that the user generate
     * @param providerType provider of identityID
     * @param username     username of the user
     * @return user created
     */
    UserModel addUser(String identityID, String providerType, String username);

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
     * @param identityID a unique identity string that the user generate
     * @return user, in case is not found return null
     */
    UserModel getUserByIdentityID(String identityID);

    /**
     * @return all users of that exists on the system
     */
    List<UserModel> getUsers();

    /**
     * @param query query to apply on search operation
     * @return all users after apply query
     */
    List<UserModel> getUsers(QueryModel query);

    /**
     * @param user bean. All data on user will be updated on the database. Use this on not EJB components
     */
    void updateUser(UserBean user);
}
