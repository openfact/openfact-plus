package org.openfact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.auth.oauth2.*;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.RefreshToken;
import org.keycloak.util.TokenUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@WebServlet("/api/login/authorize_callback")
public class OAuth2ServletCallback extends AbstractAuthorizationCodeCallbackServlet {

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential) throws ServletException, IOException {
        RefreshToken refreshToken = null;
        try {
            refreshToken = TokenUtil.getRefreshToken(credential.getRefreshToken());
        } catch (JWSInputException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode token = mapper.createObjectNode()
                .put("token_type", "Bearer")
                .put("access_token", credential.getAccessToken())
                .put("expires_in", credential.getExpirationTimeMilliseconds())
                .put("refresh_token", credential.getRefreshToken())
                .put("refresh_expires_in", refreshToken.getExpiration());
        String tokenString = mapper.writeValueAsString(token);

        String redirect = req.getParameter("redirect");
        resp.sendRedirect(redirect + "?token_json=" + tokenString);
    }

    @Override
    protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse) throws ServletException, IOException {
        resp.sendRedirect("/error");
    }

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
}
