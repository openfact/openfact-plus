package org.openfact.models;

import org.apache.xmlbeans.xml.stream.Space;
import org.arquillian.ape.rdbms.Cleanup;
import org.arquillian.ape.rdbms.CleanupStrategy;
import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.arquillian.ape.rdbms.UsingDataSet;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SpaceProviderTest extends AbstractModelTest {

    @Inject
    public SpaceProvider spaceProvider;

    @Inject
    public UserProvider userProvider;

    public UserModel user;

    @Before
    public void before() {
        user = userProvider.getByUsername("carlos");
    }

    @Test
    @UsingDataSet("users.yml")
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void createSpaceTest() {
        SpaceModel space1 = spaceProvider.addSpace("123456789", user);

        assertThat(space1).isNotNull()
                .matches(u -> u.getId() != null)
                .matches(u -> u.getAssignedId().equals("123456789"))
                .matches(u -> u.getAlias().equals("123456789"), "First alias should be assignedId");

        // get user
        SpaceModel space2 = spaceProvider.getByAssignedId("123456789");
        assertThat(space1).isEqualTo(space2);
    }

    @Test
    @UsingDataSet(value = {"users.yml", "spaces.yml"})
    public void removeSpaceTest() {
        SpaceModel space = spaceProvider.getByAssignedId("11111");

        assertThat(space).isNotNull()
                .matches(u -> u.getAssignedId().equals("11111"));

        // Remove
        boolean result = spaceProvider.removeSpace(space);

        assertThat(result).isTrue();
    }

}
