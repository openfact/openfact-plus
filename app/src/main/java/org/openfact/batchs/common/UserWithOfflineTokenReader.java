package org.openfact.batchs.common;

import org.openfact.models.ScrollableResultsModel;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;

import java.io.Serializable;
import javax.batch.api.chunk.AbstractItemReader;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class UserWithOfflineTokenReader extends AbstractItemReader {

    private ScrollableResultsModel<UserModel> users;

    @Inject
    private UserProvider userProvider;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        users = userProvider.getUsers();
    }

    @Override
    public UserModel readItem() {
        if (users.next()) {
            UserModel user = users.get();
            if (user.getOfflineRefreshToken() != null && user.getOfflineRefreshToken().trim().isEmpty()) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        try {
            users.close();
        } catch (Exception e) {
        }
    }

}
