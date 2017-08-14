package org.openfact.models;

import org.arquillian.ape.rdbms.Cleanup;
import org.arquillian.ape.rdbms.CleanupStrategy;
import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.arquillian.ape.rdbms.UsingDataSet;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JpaFileProviderTest extends AbstractModelTest {

    @Inject
    public FileProvider fileProvider;

    @Test
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void createFilesTest() throws StorageException {
        FileModel file1 = fileProvider.addFile(new byte[]{0, 1, 2, 3, 4, 5}, ".xml");

        assertThat(file1).isNotNull()
                .matches(u -> u.getId() != null)
                .matches(u -> u.getFilename().endsWith(".xml"))
                .matches(u -> u.getExtension().equals(".xml"));

        // get user
        FileModel user2 = fileProvider.getFile(file1.getId());
        assertThat(file1).isEqualTo(user2);
    }

    @Test
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void removeFile() throws StorageException {
        FileModel file = fileProvider.addFile(new byte[]{0, 1, 2, 3, 4, 5}, ".xml");

        boolean result = fileProvider.removeFile(file);

        assertThat(result).isTrue();
    }

}
