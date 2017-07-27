package org.openfact.services;

import org.junit.Test;
import org.openfact.models.Constants;
import org.openfact.representation.idm.UserRepresentation;

import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserServiceTest extends AbstractSecureTest {

    public static final String PATH = "/api/user";

    @Test
    public void testSecuredEndpoint() {
        Response response = client.build(PATH).get();
        assertThat(response.getStatus(), anyOf(equalTo(403), equalTo(401)));
    }

    @Test
    public void testGetAndCreateUser() {
        // Create user on database
        Response response = client.buildWithAuth("carlos", "password", PATH).get();
        UserRepresentation user1 = response.readEntity(UserRepresentation.class);
        response.close();

        assertThat(response.getStatus(), equalTo(200));
        assertThat(user1.getAttributes().getUsername(), equalTo("carlos"));

        // Check if user have been created
        response = client.buildWithAuth("carlos", "password", PATH).get();
        UserRepresentation user2 = response.readEntity(UserRepresentation.class);
        response.close();

        assertThat(response.getStatus(), equalTo(200));
        assertThat(user2.getAttributes().getUsername(), equalTo("carlos"));
    }

    @Test
    public void testGetAndCreateUserWithSpaceClaimed() {
        // Create user on database
        Response response = client.buildWithAuth("esteban", "password", PATH).get();
        UserRepresentation user = response.readEntity(UserRepresentation.class);
        response.close();

        assertThat(response.getStatus(), equalTo(200));
        assertThat(user.getAttributes().getUsername(), equalTo("esteban"));
        assertThat(user.getAttributes().getSpaces(), notNullValue());
        assertThat(user.getAttributes().getSpaces().size(), equalTo(1));
        assertThat(user.getAttributes().getSpaces().get(0).getType(), equalTo(Constants.USER_SPACE_TYPE_OWNER));
    }

    @Test
    public void testGetAndCreateUserWithOwnedSpaceClaimed() {
        // Create user on database
        Response response = client.buildWithAuth("carlos", "password", PATH).get();
        response.readEntity(UserRepresentation.class);
        response.close();

        assertThat(response.getStatus(), equalTo(200));

        // Create user and claim space
        response = client.buildWithAuth("esteban", "password", PATH).get();
        UserRepresentation user2 = response.readEntity(UserRepresentation.class);
        response.close();

        assertThat(response.getStatus(), equalTo(200));
//        assertThat(user2.getAttributes().getUsername(), equalTo("esteban"));
//        assertThat(user2.getAttributes().getSpaces(), notNullValue());
//        assertThat(user2.getAttributes().getSpaces().size(), equalTo(1));
//        assertThat(user2.getAttributes().getSpaces().get(0).getType(), equalTo(Constants.USER_SPACE_TYPE_MEMBER_REQUESTED));
    }

}
