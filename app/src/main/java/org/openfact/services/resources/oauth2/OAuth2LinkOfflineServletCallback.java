package org.openfact.services.resources.oauth2;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.util.TokenUtil;
import org.openfact.models.UserBean;
import org.openfact.models.UserProvider;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@WebServlet("/api/login/authorize_offline_callback")
public class OAuth2LinkOfflineServletCallback extends AbstractAuthorizationCodeCallbackServlet {

    @Inject
    private UserProvider userProvider;

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential) throws ServletException, IOException {
        boolean isOfflineToken = false;
        try {
            isOfflineToken = TokenUtil.isOfflineToken(credential.getRefreshToken());
        } catch (JWSInputException e) {
            resp.sendRedirect(req.getParameter("redirect") + "?error=Could not decode token");
        }

        if (isOfflineToken) {
            DecodedJWT decodedJWT = JWT.decode(credential.getAccessToken());
            String identityID = decodedJWT.getClaim("userID").asString();

            UserBean bean = new UserBean();
            bean.setIdentityID(identityID);
            bean.setOfflineToken(credential.getRefreshToken());
            bean.setRegistrationComplete(true);
            userProvider.updateUser(bean);

            resp.sendRedirect(req.getParameter("redirect"));
        } else {
            resp.sendRedirect(req.getParameter("redirect") + "?error=Obtained token is not offline");
        }
    }

    @Override
    protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse) throws ServletException, IOException {
        resp.sendRedirect(req.getParameter("redirect") + "?error=could not get token");
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        return OAuth2Utils.buildRedirectURL(req, "/api/login/authorize_offline_callback");
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return OAuth2Utils.buildAuthCodeFlow(Arrays.asList("openid"));
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        return UUID.randomUUID().toString();
    }
}
