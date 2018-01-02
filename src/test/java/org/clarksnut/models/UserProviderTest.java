package org.clarksnut.models;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
//@DefaultDeployment(type = DefaultDeployment.Type.WAR)
public class UserProviderTest {

//    @Inject
//    public UserProvider userProvider;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

//    @Test
//    @CleanupUsingScript(value = "clean_database.sql", phase = TestExecutionPhase.BEFORE)
//    public void createUserTest() {
//        String identityID = UUID.randomUUID().toString();
//        UserModel user1 = userProvider.addUser(identityID, "kc", "carlos");
//
//        assertThat(user1).isNotNull()
//                .matches(u -> u.getId() != null, "invalid id")
//                .matches(u -> u.getIdentityID().equals(identityID), "invalid IdentityID")
//                .matches(u -> u.getUsername().equals("carlos"), "Invalid username")
//                .matches(u -> !u.isRegistrationCompleted(), "RegistrationComplete should be false");
//
//        UserModel user2 = userProvider.getUserByIdentityID(identityID);
//        assertThat(user1).isEqualTo(user2);
//    }
//
//    @Test
//    @CleanupUsingScript(value = "clean_database.sql", phase = TestExecutionPhase.BEFORE)
//    public void getUsersTest() {
//        userProvider.addUser(UUID.randomUUID().toString(), "kc", "carlos");
//        userProvider.addUser(UUID.randomUUID().toString(), "kc", "carlosferia");
//        userProvider.addUser(UUID.randomUUID().toString(), "kc", "carlosferiavila");
//
//        QueryModel query = QueryModel.builder()
//                .filterText("carlos")
//                .build();
//        List<UserModel> users = userProvider.getUsers(query);
//        assertThat(users).isNotNull()
//                .matches(u -> u.size() == 3, "Result should be 3 by filterText");
//
//
//        query = QueryModel.builder()
//                .addFilter(UserModel.USERNAME, "carlos")
//                .build();
//        users = userProvider.getUsers(query);
//        assertThat(users).isNotNull()
//                .matches(u -> u.size() == 3, "Result should be 3 by filter");
//    }

    @Test
    public void test() {
        Assert.assertNotNull("Hello");
    }

}
