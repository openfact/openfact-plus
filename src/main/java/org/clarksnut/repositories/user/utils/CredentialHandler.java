package org.clarksnut.repositories.user.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequest;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.clarksnut.services.resources.KeycloakDeploymentConfig;
import org.keycloak.adapters.KeycloakDeployment;

import java.lang.reflect.Method;

public class CredentialHandler implements MethodInterceptor {

    private final String broker;
    private final Credential credential;

    public CredentialHandler(String broker, Credential credential) {
        this.broker = broker;
        this.credential = credential;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (method.getName().equals("initialize")) {
            HttpRequest request = (HttpRequest) args[0];

            KeycloakDeployment keycloakDeployment = KeycloakDeploymentConfig.getInstance().getDeployment();
            KeycloakBrokerHttpInterceptor interceptor = new KeycloakBrokerHttpInterceptor(
                    keycloakDeployment.getAuthServerBaseUrl(), keycloakDeployment.getRealm(), broker, credential);

            request.setInterceptor(interceptor);
            request.setUnsuccessfulResponseHandler(interceptor);
            return null;
        }
        return method.invoke(credential, args);
    }

}
