package org.openfact.repositories.user.gmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequest;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CredentialHandler implements MethodInterceptor {

    private final Credential credential;

    public CredentialHandler(Credential credential) {
        this.credential = credential;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (method.getName().equals("initialize")) {
            HttpRequest request = (HttpRequest) args[0];
            KeycloakHttpExecuteInterceptor interceptor = new KeycloakHttpExecuteInterceptor(credential);
            request.setInterceptor(interceptor);
            request.setUnsuccessfulResponseHandler(interceptor);
            return null;
        }
        return method.invoke(credential, args);
    }

}
