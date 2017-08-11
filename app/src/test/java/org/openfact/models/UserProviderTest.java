package org.openfact.models;

import org.arquillian.ape.rdbms.Cleanup;
import org.arquillian.ape.rdbms.CleanupStrategy;
import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.arquillian.ape.rdbms.UsingDataSet;
import org.junit.Test;

import javax.inject.Inject;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserProviderTest extends AbstractModelTest {

    @Inject
    public UserProvider userProvider;

    @Test(expected = ModelDuplicateException.class)
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void createUserTest() {
        UserModel user = userProvider.addUser("feria");

        assertThat(user).isNotNull()
                .matches(u -> u.getId() != null)
                .matches(u -> u.getUsername().equals("feria"))
                .matches(u -> !u.isRegistrationCompleted());

        user = userProvider.getByUsername("feria");
        assertThat(user).isNotNull();

        userProvider.addUser("feria");
    }

    @Test
    @UsingDataSet("users.yml")
    public void getByUsernameTest() {
        UserModel user = userProvider.getByUsername("carlos");

        assertThat(user).isNotNull()
                .matches(u -> u.getUsername().equals("carlos"));
    }

    @Test
    @UsingDataSet("users.yml")
    public void getUsersTest() {
        List<UserModel> users = userProvider.getUsers();

        assertThat(users.size()).isEqualTo(5);
    }

    @Test
    @UsingDataSet("users.yml")
    public void getScrollableUsersTest() {
        ScrollableResultsModel<UserModel> users = userProvider.getScrollableUsers();

        int currentRowNumber = 0;
        if (users.next()) {
            currentRowNumber++;
        }
        users.close();

        assertThat(currentRowNumber).isEqualTo(5);
    }

}
