package org.openfact.services.managers;

import org.jboss.logging.Logger;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.ServerRequest;
import org.keycloak.representations.AccessTokenResponse;
import org.openfact.models.BrokerType;
import org.openfact.models.UserModel;
import org.openfact.models.UserRepositoryModel;
import org.openfact.repositories.user.UserRepositorySync;
import org.openfact.services.resources.UsersService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class UserManager {

    private static final Logger logger = Logger.getLogger(UsersService.class);

    @Inject
    private BrokerManager brokerManager;

    @Inject
    private UserRepositorySync repositorySynchronizer;

    /**
     * Validate user offline token
     *
     * @param user
     * @return user has valid offline refresh token
     */
//    public boolean hasValidOfflineRefreshToken(UserModel user) {
//        String offlineRefreshToken = user.getOfflineRefreshToken();
//
//        if (offlineRefreshToken == null) {
//            return false;
//        }
//
//        // Check offline token is still valid
//        String accessToken = null;
//        try {
//            accessToken = retrieveAccessToken(offlineRefreshToken);
//        } catch (IOException | ServerRequest.HttpFailure e) {
//            logger.warn("Invalid token detected, try to delete it in order to reduce server process");
//        }
//
//        return accessToken != null;
//    }
//
//    private String retrieveAccessToken(String refreshToken) throws IOException, ServerRequest.HttpFailure {
//        AccessTokenResponse response = ServerRequest.invokeRefresh(keycloakDeployment, refreshToken);
//        return response.getToken();
//    }

    /**
     * Refresh repositories based on offline token.
     * Be careful because this option will delete and add new repositories
     * depending of brokers associated to the token
     *
     * @param user
     */
//    public void refreshUserAvailableRepositories(UserModel user) {
//        if (hasValidOfflineRefreshToken(user)) {
//            List<BrokerType> currentBrokers = user.getRepositories().stream()
//                    .read(UserRepositoryModel::getType)
//                    .collect(Collectors.toList());
//
//            brokerManager.getAvailableBrokers(user.getOfflineRefreshToken()).stream()
//                    .filter(p -> !currentBrokers.contains(p.getType()))
//                    .forEach(c -> {
//                        user.addRepository(c.getEmail(), c.getType());
//                    });
//        } else {
//            user.removeAllRepositories();
//        }
//    }

    /**
     * Synchronize repositories
     *
     * @param user
     */
//    public void syncUserRepositories(UserModel user) {
//        for (UserRepositoryModel repo : user.getRepositories()) {
//            repositorySynchronizer.synchronize(repo);
//        }
//    }

}
