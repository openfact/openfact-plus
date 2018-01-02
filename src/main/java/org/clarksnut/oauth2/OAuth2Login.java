package org.clarksnut.oauth2;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet("/api/login/authorize")
public class OAuth2Login extends AbstractAuthorizationCodeServlet {

    public static final List<String> SCOPES = Collections.singletonList("openid");
    public static final String CALLBACK = "/api/login/authorize_callback";

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        String redirect = OAuth2Utils.getRedirect(req);
        return OAuth2Utils.buildRedirectURL(req, CALLBACK, redirect);
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return OAuth2Utils.getAuthorizationCodeFlow(SCOPES);
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        return null;
    }

}
