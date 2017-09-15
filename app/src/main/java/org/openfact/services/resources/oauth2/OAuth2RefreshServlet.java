package org.openfact.services.resources.oauth2;

import com.google.api.client.auth.oauth2.Credential;
import org.codehaus.jackson.map.ObjectMapper;
import org.openfact.representation.idm.TokenRepresentation;

import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet("/api/login/refresh")
public class OAuth2RefreshServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Read request
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String json = br.readLine();

        ObjectMapper mapper = new ObjectMapper();
        TokenRepresentation tokenRep = mapper.readValue(json, TokenRepresentation.class);

        // Refresh token
        Credential credential = OAuth2Utils.buildCredential().setRefreshToken(tokenRep.getRefresh_token());
        boolean result;
        try {
            result = credential.refreshToken();
        } catch (IOException e) {
            throw new InternalServerErrorException();
        }
        if (!result) {
            throw new BadRequestException();
        }

        // Result
        resp.setContentType("application/json");
        mapper.writeValue(resp.getOutputStream(), OAuth2Utils.toRepresentation(credential));
    }

}
