package org.openfact.models;

import org.arquillian.ape.rdbms.*;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class SpaceProviderTest extends AbstractModelTest {

    @Inject
    public SpaceProvider spaceProvider;

    @Inject
    public UserProvider userProvider;

    public UserModel user;

    @Before
    public void before() {
        user = userProvider.getUserByIdentityID("carlos");
    }

    @Test
    @UsingDataSet("users.yml")
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void createSpaceTest() {
        SpaceModel space1 = spaceProvider.addSpace("123456789", user);

        assertThat(space1).isNotNull()
                .matches(u -> u.getId() != null)
                .matches(u -> u.getAssignedId().equals("123456789"))
                .matches(u -> u.getName().equals("123456789"), "First alias should be assignedId");

        // getInstance user
        SpaceModel space2 = spaceProvider.getByAssignedId("123456789");
        assertThat(space1).isEqualTo(space2);
    }

    @Test
    @UsingDataSet(value = {"users.yml"})
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void removeSpaceTest() {
        SpaceModel space = spaceProvider.addSpace("123456789", user);

        assertThat(space).isNotNull()
                .matches(u -> u.getAssignedId().equals("123456789"));

        boolean result = spaceProvider.removeSpace(space);

        assertThat(result).isTrue();
    }

}
