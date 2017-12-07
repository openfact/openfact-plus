package org.clarksnut.models;

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
     * @param query query to apply on search operation
     * @return all users after apply query
     */
    List<UserModel> getUsers(QueryModel query);

    /**
     * @return all users who has offline token
     */
    List<UserModel> getUsersWithOfflineToken();

    /**
     * @return all users who has offline token
     */
    List<UserModel> getUsersWithOfflineToken(int offset, int limit);

    /**
     * @param user bean. All data on user will be updated on the database. Use this on not EJB components. Null Fields will never be changed
     */
    void updateUser(UserBean user);
}
