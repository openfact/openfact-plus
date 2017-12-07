package org.clarksnut.oauth2;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import org.clarksnut.models.UserBean;
import org.clarksnut.models.UserProvider;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.util.TokenUtil;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/login/authorize_offline_callback")
public class OAuth2LinkUserOfflineRefreshTokenCallback extends AbstractAuthorizationCodeCallbackServlet {

    @Inject
    private UserProvider userProvider;

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential) throws ServletException, IOException {
        String redirect = OAuth2Utils.getRedirect(req);

        boolean isOfflineToken = false;
        try {
            isOfflineToken = TokenUtil.isOfflineToken(credential.getRefreshToken());
        } catch (JWSInputException e) {
            resp.sendRedirect(redirect + "?error=Could not decode token");
        }

        if (isOfflineToken) {
            DecodedJWT decodedJWT = JWT.decode(credential.getAccessToken());
            String identityID = decodedJWT.getClaim("userID").asString();

            UserBean bean = new UserBean();
            bean.setIdentityID(identityID);
            bean.setOfflineToken(credential.getRefreshToken());
            bean.setRegistrationComplete(true);
            userProvider.updateUser(bean);

            resp.sendRedirect(redirect);
        } else {
            resp.sendRedirect(redirect + "?error=Obtained token is not offline");
        }
    }

    @Override
    protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse) throws ServletException, IOException {
        String redirect = OAuth2Utils.getRedirect(req);
        resp.sendRedirect(redirect + "?error=could not get token");
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        String redirect = OAuth2Utils.getRedirect(req);
        return OAuth2Utils.buildRedirectURL(req, OAuth2LinkUserOfflineRefreshToken.CALLBACK, redirect);
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return OAuth2Utils.getAuthorizationCodeFlow(OAuth2LinkUserOfflineRefreshToken.SCOPES);
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        return null;
    }
}
