package org.openfact.services;

import org.jboss.arquillian.persistence.UsingDataSet;
import org.junit.Test;
import org.openfact.models.RequestStatusType;
import org.openfact.representation.idm.UserRepresentation;

import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserServiceTest extends AbstractSecureTest {

    public static final String PATH = "/api/user";

    @Test
    public void testSecuredEndpoint() {
        Response response = client.build(PATH).get();
        assertThat(response.getStatus(), anyOf(equalTo(403), equalTo(401)));
    }

    @Test
    @UsingDataSet("empty.xml")
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
    @UsingDataSet("empty.xml")
    public void testGetAndCreateUserWithSpaceClaimed() {
        // Create user on database
        Response response = client.buildWithAuth("esteban", "password", PATH).get();
        UserRepresentation user = response.readEntity(UserRepresentation.class);
        response.close();

        assertThat(response.getStatus(), equalTo(200));
        assertThat(user.getAttributes().getUsername(), equalTo("esteban"));
        assertThat(user.getAttributes().getOwnedSpaces(), notNullValue());
        assertThat(user.getAttributes().getOwnedSpaces().size(), equalTo(1));
        assertThat(user.getAttributes().getOwnedSpaces().get(0).getAssignedId(), equalTo("00000000"));
    }

    @Test
    @UsingDataSet("empty.xml")
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
        assertThat(user2.getAttributes().getUsername(), equalTo("esteban"));
        assertThat(user2.getAttributes().getOwnedSpaces().size(), equalTo(0));
        assertThat(user2.getAttributes().getSharedSpaces().size(), equalTo(0));
        assertThat(user2.getAttributes().getSpaceRequests(), notNullValue());
        assertThat(user2.getAttributes().getSpaceRequests().size(), equalTo(1));
        assertThat(user2.getAttributes().getSpaceRequests().get(0).getSpaceId(), notNullValue());
        assertThat(user2.getAttributes().getSpaceRequests().get(0).getSpaceAssignedId(), equalTo("00000000"));
        assertThat(user2.getAttributes().getSpaceRequests().get(0).getStatus(), equalTo(RequestStatusType.REQUESTED.toString().toLowerCase()));
    }

}
