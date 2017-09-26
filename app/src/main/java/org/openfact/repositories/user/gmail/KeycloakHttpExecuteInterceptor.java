package org.openfact.repositories.user.gmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.http.*;
import org.keycloak.adapters.KeycloakDeployment;
import org.openfact.representation.idm.TokenRepresentation;
import org.openfact.services.resources.KeycloakDeploymentConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KeycloakHttpExecuteInterceptor implements HttpExecuteInterceptor, HttpUnsuccessfulResponseHandler {

    private final Credential credential;

    private final Lock lock = new ReentrantLock();
    private String keycloakAccessToken;
    private Long keycloakExpirationTimeMilliseconds;

    public KeycloakHttpExecuteInterceptor(Credential credential) {
        this.credential = credential;
    }

    @Override
    public void intercept(HttpRequest request) throws IOException {
        String oldAccessToken = credential.getAccessToken();
        credential.intercept(request);
        String newAccessToken = credential.getAccessToken();

        lock.lock();
        try {
            Long keycloakExpiresIn = getKeycloakExpiresInSeconds();
            if (!newAccessToken.equals(oldAccessToken) || keycloakAccessToken == null || keycloakExpiresIn != null && keycloakExpiresIn <= 60) {
                refreshKeycloakToken();
                if (keycloakAccessToken == null) {
                    // nothing we can do without an access token
                    return;
                }
            }
            credential.getMethod().intercept(request, keycloakAccessToken);
        } finally {
            lock.unlock();
        }
    }

    private boolean refreshKeycloakToken() throws IOException {
        lock.lock();
        try {
            try {
                TokenRepresentation tokenResponse = executeKeycloakRefreshToken();
                if (tokenResponse != null) {
                    setFromKeycloakTokenResponse(tokenResponse);
                    return true;
                }
            } catch (TokenResponseException e) {
                boolean statusCode4xx = 400 <= e.getStatusCode() && e.getStatusCode() < 500;
                // check if it is a normal error response
                if (e.getDetails() != null && statusCode4xx) {
                    // We were unable to get a new access token (e.g. it may have been revoked), we must now
                    // indicate that our current token is invalid.
                    setKeycloakAccessToken(null);
                    setKeycloakExpiresInSeconds(null);
                }
                if (statusCode4xx) {
                    throw e;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private TokenRepresentation executeKeycloakRefreshToken() throws IOException {
        if (credential.getAccessToken() == null) {
            return null;
        }
        ClientRequestFilter authFilter = requestContext -> {
            requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + credential.getAccessToken());
            requestContext.getHeaders().add(HttpHeaders.ACCEPT, "application/json");
        };

        Client client = ClientBuilder.newBuilder().register(authFilter).build();
        WebTarget target = client.target(getIdentityProviderTokenUrl("google"));

        return target.request().get().readEntity(TokenRepresentation.class);
    }

    private String getIdentityProviderTokenUrl(String broker) {
        KeycloakDeployment deployment = KeycloakDeploymentConfig.getInstance().getDeployment();
        return deployment.getAuthServerBaseUrl() + "/realms/" + deployment.getRealm() + "/broker/" + broker + "/token";
    }

    private void setFromKeycloakTokenResponse(TokenRepresentation tokenResponse) {
        setKeycloakAccessToken(tokenResponse.getAccess_token());
        setKeycloakExpiresInSeconds(tokenResponse.getExpires_in());
    }

    private void setKeycloakAccessToken(String accessToken) {
        lock.lock();
        try {
            this.keycloakAccessToken = accessToken;
        } finally {
            lock.unlock();
        }
    }

    private void setKeycloakExpiresInSeconds(Long expiresIn) {
        setKeycloakExpirationTimeMilliseconds(expiresIn == null ? null : credential.getClock().currentTimeMillis() + expiresIn * 1000);
    }

    private void setKeycloakExpirationTimeMilliseconds(Long expirationTimeMilliseconds) {
        lock.lock();
        try {
            this.keycloakExpirationTimeMilliseconds = expirationTimeMilliseconds;
        } finally {
            lock.unlock();
        }
    }

    public Long getKeycloakExpiresInSeconds() {
        lock.lock();
        try {
            if (keycloakExpirationTimeMilliseconds == null) {
                return null;
            }
            return (keycloakExpirationTimeMilliseconds - credential.getClock().currentTimeMillis()) / 1000;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean handleResponse(HttpRequest request, HttpResponse response, boolean supportsRetry) throws IOException {
        return credential.handleResponse(request, response, supportsRetry);
    }
}
