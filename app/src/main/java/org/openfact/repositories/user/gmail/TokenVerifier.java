package org.openfact.repositories.user.gmail;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenVerifier {

    public GoogleIdToken getGoogleIdToken(String bearerToken) throws GeneralSecurityException, IOException {
        JsonFactory factory = new JacksonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier(new ApacheHttpTransport(), factory);
        return GoogleIdToken.parse(factory, bearerToken);
    }

}
