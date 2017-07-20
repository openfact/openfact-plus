package org.openfact;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openfact.representation.idm.UserRepresentation;
import org.openfact.services.KeycloakUtil;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(Arquillian.class)
@DefaultDeployment
public class UserServiceTest {

    @Test
    @RunAsClient
    public void getUserAndCreate() {
        KeycloakUtil kcUtil = mock(KeycloakUtil.class);
        when(kcUtil.getUsername()).thenReturn("carlosthe19916");

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080").path("api/user");

        verify(kcUtil).getUsername();

        Response response = target.request(MediaType.APPLICATION_JSON).get();

        // Check status
        assertThat(response.getStatus(), equalTo(200));

        // Check result
        UserRepresentation representation = response.readEntity(UserRepresentation.class);

        assertThat(representation, is(notNullValue()));
        assertThat(representation.getAttributes().getUsername(), equalTo("carlosthe19916"));
    }

}
