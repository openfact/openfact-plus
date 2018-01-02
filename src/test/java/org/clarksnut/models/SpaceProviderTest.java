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
public class SpaceProviderTest {

//    public UserModel user;
//
//    @Inject
//    public SpaceProvider spaceProvider;
//
//    @Inject
//    public UserProvider userProvider;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }


//    @Before
//    public void before() {
//        user = userProvider.addUser(UUID.randomUUID().toString(), "kc", "carlos");
//    }
//
//    @Test
//    @CleanupUsingScript(value = "clean_database.sql", phase = TestExecutionPhase.BEFORE)
//    public void createSpaceTest() {
//        SpaceModel space1 = spaceProvider.addSpace("123456789", "sistcoop", user);
//
//        assertThat(space1).isNotNull()
//                .matches(u -> u.getId() != null, "invalid id")
//                .matches(u -> u.getAssignedId().equals("123456789"), "invalid assignedId")
//                .matches(u -> u.getName().equals("sistcoop"), "invalid space name");
//
//        SpaceModel space2 = spaceProvider.getByAssignedId("123456789");
//        assertThat(space1).isEqualTo(space2);
//    }
//
//    @Test
//    @CleanupUsingScript(value = "clean_database.sql", phase = TestExecutionPhase.BEFORE)
//    public void removeSpaceTest() {
//        SpaceModel space = spaceProvider.addSpace("123456789", "sistcoop", user);
//        boolean result = spaceProvider.removeSpace(space);
//        assertThat(result).isTrue();
//    }
//
//    @Test
//    @CleanupUsingScript(value = "clean_database.sql", phase = TestExecutionPhase.BEFORE)
//    public void getSpacesTest() {
//        createSpaces();
//
//        List<SpaceModel> spaces = spaceProvider.getSpaces(user);
//        assertThat(spaces).isNotNull()
//                .matches(u -> u.size() == 2, "User should've owned 2 spaces");
//
//        spaces = spaceProvider.getSpaces(user, 1, 1);
//        assertThat(spaces).isNotNull()
//                .matches(u -> u.size() == 1, "User should've owned 2 spaces but just one showed here");
//    }
//
//    @Test
//    @CleanupUsingScript(value = "clean_database.sql", phase = TestExecutionPhase.BEFORE)
//    public void getSpacesByQueryTest() {
//        createSpaces();
//
//        QueryModel query = QueryModel.builder()
//                .filterText("sistcoop")
//                .build();
//        List<SpaceModel> spaces = spaceProvider.getSpaces(query);
//        assertThat(spaces).isNotNull()
//                .matches(u -> u.size() == 1, "Result should be 1 by filterText(name)");
//
//        query = QueryModel.builder()
//                .filterText("00000")
//                .build();
//        spaces = spaceProvider.getSpaces(query);
//        assertThat(spaces).isNotNull()
//                .matches(u -> u.size() == 1, "Result should be 1 by filterText(assignedId)");
//
//        query = QueryModel.builder()
//                .addFilter(SpaceModel.NAME, "sistcoop")
//                .addFilter(SpaceModel.ASSIGNED_ID, "00000")
//                .build();
//        spaces = spaceProvider.getSpaces(query);
//        assertThat(spaces).isNotNull()
//                .matches(u -> u.size() == 1, "Result should be 1 by filter(name AND assignedId)");
//    }
//
//    private void createSpaces() {
//        spaceProvider.addSpace("000000000", "sistcoop", user);
//        spaceProvider.addSpace("111111111", "softgreen", user);
//
//        UserModel user2 = userProvider.addUser(UUID.randomUUID().toString(), "kc", "esteban");
//        spaceProvider.addSpace("222222222", "abc", user2);
//    }

    @Test
    public void test() {
        Assert.assertNotNull("Hello");
    }

}
