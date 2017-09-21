package org.openfact.batchs.refresh;

import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.ServerRequest;
import org.keycloak.representations.AccessTokenResponse;
import org.openfact.models.BrokerType;
import org.openfact.models.UserModel;
import org.openfact.models.UserRepositoryModel;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named
@Dependent
public class OfflineRefreshTokenProcessor implements ItemProcessor {

    @Override
    public MappedUser processItem(Object item) throws Exception {
//        UserModel user;
//        if (item instanceof UserModel) {
//            user = (UserModel) item;
//        } else {
//            return null;
//        }
//
//        // Get offline refresh token
//        String offlineRefreshToken = user.getOfflineRefreshToken();
//
//        // Check offline token is still valid
//        String accessToken = null;
//        try {
//            accessToken = retrieveAccessToken(offlineRefreshToken);
//        } catch (ServerRequest.HttpFailure e) {
//            // Invalid refresh token
//            user.setOfflineRefreshToken(null);
//            user.removeAllRepositories();
//        }
//        if (accessToken == null) {
//            return null;
//        }
//
//        // Check if user has already had supported repositories
//        List<BrokerType> supportedIDPS = Arrays.asList(BrokerType.values());
//        boolean result = user.getRepositories().stream().read(UserRepositoryModel::getType).allMatch(supportedIDPS::contains);
//        if (result) {
//            return null;
//        }
//        supportedIDPS.removeAll(user.getRepositories().stream().read(UserRepositoryModel::getType).collect(Collectors.toList()));
//        return new MappedUser(user, supportedIDPS);
        return null;
    }

    private String retrieveAccessToken(String refreshToken) throws IOException, ServerRequest.HttpFailure {
        KeycloakDeployment deployment = null;
        AccessTokenResponse response = ServerRequest.invokeRefresh(deployment, refreshToken);
        return response.getToken();
    }

    public static class MappedUser {
        private final UserModel user;
        private final List<BrokerType> idps;

        public MappedUser(UserModel user, List<BrokerType> idps) {
            this.user = user;
            this.idps = idps;
        }

        public UserModel getUser() {
            return user;
        }

        public List<BrokerType> getIdps() {
            return idps;
        }
    }

}
