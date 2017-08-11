package org.openfact.services.rule;

import org.junit.rules.ExternalResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.test.TestsHelper;
import org.keycloak.test.builders.ClientBuilder;
import org.openfact.models.Constants;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static org.keycloak.test.TestsHelper.deleteRealm;
import static org.keycloak.test.TestsHelper.importTestRealm;
import static org.keycloak.test.builders.ClientBuilder.AccessType.PUBLIC;

public class KeycloakRule extends ExternalResource {

    @Override
    protected void before() {
        try {
            TestsHelper.appName = "arquillian-test";
            TestsHelper.keycloakBaseUrl = "http://localhost:8180/auth"; // SSO url
            try {
                deleteRealm("admin", "admin", "test-realm");
            } catch (Throwable e) {
            }
            importTestRealm("admin", "admin", "/test-realm.json");
            createDirectGrantClientWithMappers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String createDirectGrantClientWithMappers() {
        ProtocolMapperRepresentation usernameProtocolMapper = new ProtocolMapperRepresentation();
        usernameProtocolMapper.setName("username");
        usernameProtocolMapper.setConsentText("${username}");
        usernameProtocolMapper.setProtocol("openid-connect");
        usernameProtocolMapper.setProtocolMapper("oidc-usermodel-property-mapper");
        usernameProtocolMapper.setConsentRequired(true);
        usernameProtocolMapper.setConfig(new HashMap<String, String>() {{
            put("user.attribute", "username");
            put("claim.name", "preferred_username");
            put("id.token.claim", "true");
            put("access.token.claim", "true");
            put("userinfo.token.claim", "true");
            put("jsonType.label", "String");
        }});

        ProtocolMapperRepresentation spaceProtocolMapper = new ProtocolMapperRepresentation();
        spaceProtocolMapper.setName(Constants.KC_SPACE_ATTRIBUTE_NAME);
        spaceProtocolMapper.setProtocol("openid-connect");
        spaceProtocolMapper.setProtocolMapper("oidc-usermodel-attribute-mapper");
        spaceProtocolMapper.setConsentRequired(false);
        spaceProtocolMapper.setConfig(new HashMap<String, String>() {{
            put("user.attribute", Constants.KC_SPACE_ATTRIBUTE_NAME);
            put("claim.name", Constants.KC_SPACE_ATTRIBUTE_NAME);
            put("id.token.claim", "true");
            put("access.token.claim", "true");
            put("jsonType.label", "String");
        }});

        ClientRepresentation clientRep = ClientBuilder.create("test-dga").accessType(PUBLIC);
        clientRep.setProtocolMappers(Arrays.asList(usernameProtocolMapper, spaceProtocolMapper));
        return TestsHelper.createClient(clientRep);
    }

    @Override
    protected void after() {
        try {
            cleanUp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void cleanUp() throws IOException {
        deleteRealm("admin", "admin", TestsHelper.testRealm);
    }

}
