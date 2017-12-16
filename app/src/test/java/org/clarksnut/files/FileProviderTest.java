package org.clarksnut.files;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
//@DefaultDeployment(type = DefaultDeployment.Type.WAR)
public class FileProviderTest {

//    @Inject
//    public FileProvider fileProvider;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

//    @Test
//    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
//    public void createFilesTest() throws FileStorageException {
//        FileModel file1 = fileProvider.addFile(new byte[]{0, 1, 2, 3, 4, 5}, ".xml");
//
//        assertThat(file1).isNotNull()
//                .matches(u -> u.getId() != null)
//                .matches(u -> u.getFilename().endsWith(".xml"))
//                .matches(u -> u.getExtension().equals(".xml"));
//
//        // getInstance user
//        FileModel user2 = fileProvider.getFile(file1.getId());
//        assertThat(file1).isEqualTo(user2);
//    }
//
//    @Test
//    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
//    public void removeFile() throws FileStorageException {
//        FileModel file = fileProvider.addFile(new byte[]{0, 1, 2, 3, 4, 5}, ".xml");
//
//        boolean result = fileProvider.removeFile(file);
//
//        assertThat(result).isTrue();
//    }

    @Test
    public void test() {

    }

}
