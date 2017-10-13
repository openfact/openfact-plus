package org.openfact.files;

import org.arquillian.ape.rdbms.Cleanup;
import org.arquillian.ape.rdbms.CleanupStrategy;
import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.WAR)
public class FileProviderTest {

    @Inject
    public FileProvider fileProvider;

    @Test
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void createFilesTest() throws ModelStorageException {
        FileModel file1 = fileProvider.addFile(new byte[]{0, 1, 2, 3, 4, 5}, ".xml");

        assertThat(file1).isNotNull()
                .matches(u -> u.getId() != null)
                .matches(u -> u.getFilename().endsWith(".xml"))
                .matches(u -> u.getExtension().equals(".xml"));

        // getInstance user
        FileModel user2 = fileProvider.getFile(file1.getId());
        assertThat(file1).isEqualTo(user2);
    }

    @Test
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void removeFile() throws ModelStorageException {
        FileModel file = fileProvider.addFile(new byte[]{0, 1, 2, 3, 4, 5}, ".xml");

        boolean result = fileProvider.removeFile(file);

        assertThat(result).isTrue();
    }

}
