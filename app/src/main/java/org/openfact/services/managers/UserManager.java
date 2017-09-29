package org.openfact.services.managers;

import org.jboss.logging.Logger;
import org.openfact.services.resources.UsersService;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class UserManager {

    private static final Logger logger = Logger.getLogger(UsersService.class);

    @Inject
    private BrokerManager brokerManager;

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
//                    .read(UserRepositoryModel::getDocumentType)
//                    .collect(Collectors.toList());
//
//            brokerManager.getAvailableBrokers(user.getOfflineRefreshToken()).stream()
//                    .filter(p -> !currentBrokers.contains(p.getDocumentType()))
//                    .forEach(c -> {
//                        user.addRepository(c.getEmail(), c.getDocumentType());
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
