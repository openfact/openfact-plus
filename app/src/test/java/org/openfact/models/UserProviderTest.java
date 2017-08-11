package org.openfact.models;

import org.arquillian.ape.rdbms.Cleanup;
import org.arquillian.ape.rdbms.CleanupStrategy;
import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.junit.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class UserProviderTest extends AbstractModelTest {

    @Inject
    public UserProvider userProvider;

    @Test
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void createSpaceTest() {
        UserModel user = userProvider.addUser("carlos");

        assertThat(user).isNotNull()
                .matches(u -> u.getId() != null)
                .matches(u -> u.getUsername().equals("carlos"))
                .matches(u -> !u.isRegistrationCompleted());
    }

}
