package org.openfact.models;

import org.arquillian.ape.rdbms.Cleanup;
import org.arquillian.ape.rdbms.CleanupStrategy;
import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.arquillian.ape.rdbms.UsingDataSet;
import org.junit.Test;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserProviderTest extends AbstractModelTest {

    @Inject
    public UserProvider userProvider;

    @Test
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void createUserTest() {
        UserModel user1 = userProvider.addUser("carlos");

        assertThat(user1).isNotNull()
                .matches(u -> u.getId() != null)
                .matches(u -> u.getUsername().equals("carlos"))
                .matches(u -> !u.isRegistrationCompleted());

        // get user
        UserModel user2 = userProvider.getByUsername("carlos");
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    @UsingDataSet("users.yml")
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void getByUsernameTest() {
        UserModel user = userProvider.getByUsername("carlos");

        assertThat(user).isNotNull()
                .matches(u -> u.getUsername().equals("carlos"));
    }

    @Test
    @UsingDataSet("users.yml")
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void getUsersTest() {
        List<UserModel> users = userProvider.getUsers();

        assertThat(users.size()).isEqualTo(5);
    }

    @Test
    @UsingDataSet("users.yml")
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void getScrollableUsersTest() {
        ScrollableResultsModel<UserModel> users = userProvider.getScrollableUsers();

        assertThat(users).isNotNull();

        while (users.next()) {
            assertThat(users.get()).isNotNull();
        }
        users.close();
    }

}
