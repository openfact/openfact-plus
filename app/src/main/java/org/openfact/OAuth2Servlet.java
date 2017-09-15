package org.openfact;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@WebServlet("/api/login/authorize")
public class OAuth2Servlet extends AbstractAuthorizationCodeServlet {

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        return OAuth2Utils.buildRedirectURL(req);
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return OAuth2Utils.buildAuthCodeFlow(Arrays.asList("openid"));
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        return UUID.randomUUID().toString();
    }

//    @Override
//    protected void onAuthorization(HttpServletRequest req, HttpServletResponse resp,
//                                   AuthorizationCodeRequestUrl authorizationUrl) throws ServletException, IOException {
//        authorizationUrl.setState(UUID.randomUUID().toString());
//        super.onAuthorization(req, resp, authorizationUrl);
//    }
}
