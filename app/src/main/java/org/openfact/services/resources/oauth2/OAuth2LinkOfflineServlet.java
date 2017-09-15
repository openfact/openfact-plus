package org.openfact.services.resources.oauth2;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@WebServlet("/api/login/linkoffline")
public class OAuth2LinkOfflineServlet extends AbstractAuthorizationCodeServlet {

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        return OAuth2Utils.buildRedirectURL(req, "/api/login/authorize_offline_callback");
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return OAuth2Utils.buildAuthCodeFlow(Arrays.asList("openid", "offline_access"));
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        return UUID.randomUUID().toString();
    }

}
