package org.clarksnut.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import org.clarksnut.representations.idm.TokenRepresentation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/login/authorize_callback")
public class OAuth2LoginCallback extends AbstractAuthorizationCodeCallbackServlet {

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        TokenRepresentation token = OAuth2Utils.toRepresentation(credential);

        String redirect = OAuth2Utils.getRedirect(req);
        resp.sendRedirect(redirect + "?token_json=" + mapper.writeValueAsString(token));
    }

    @Override
    protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse) throws ServletException, IOException {
        String redirect = OAuth2Utils.getRedirect(req);
        resp.sendRedirect(redirect + "?error=could not get token");
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        String redirect = OAuth2Utils.getRedirect(req);
        return OAuth2Utils.buildRedirectURL(req, OAuth2Login.CALLBACK, redirect);
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return OAuth2Utils.getAuthorizationCodeFlow(OAuth2Login.SCOPES);
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        return null;
    }

}
