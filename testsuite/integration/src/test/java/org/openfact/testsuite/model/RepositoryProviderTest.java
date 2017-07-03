package org.openfact.testsuite.model;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.openfact.models.ModelDuplicateException;
import org.openfact.models.RepositoryModel;
import org.openfact.models.RepositoryProvider;
import org.openfact.models.jpa.JpaRepositoryProvider;
import org.openfact.testsuite.TestUtil;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class RepositoryProviderTest extends AbstractModelTest {

    public RepositoryModel REPOSITORY;

    @Inject
    public RepositoryProvider repositoryProvider;

    @Deployment
    public static Archive deploy() {
        Archive[] libs = TestUtil.getLibraries();
        WebArchive archive = buildArchive()
                .addClass(RepositoryModel.class)
                .addClass(RepositoryProvider.class)
                .addClass(JpaRepositoryProvider.class);
        return archive.addAsLibraries(libs);
    }

    @Before
    public void before() {
        REPOSITORY = new RepositoryModel();
        REPOSITORY.setAlias("Alias");
        REPOSITORY.setEnabled(true);
        REPOSITORY.setRepositoryId("repositoryId");
        REPOSITORY.setConfig(new HashMap<String, String>() {{
            put("key1", "value1");
            put("key2", "value2");
            put("key3", "value3");
        }});
    }

    @Test
    public void create() {
        repositoryProvider.addRepository(REPOSITORY);

        assertThat(REPOSITORY, is(notNullValue()));
        assertThat(REPOSITORY.getInternalId(), is(notNullValue()));
    }

    @Test(expected = ModelDuplicateException.class)
    public void createDuplicate() {
        repositoryProvider.addRepository(REPOSITORY);
        repositoryProvider.addRepository(REPOSITORY);
    }

    @Test
    public void getRepositoryByAlias() {
        // Create Repository
        repositoryProvider.addRepository(REPOSITORY);

        // Get repository
        RepositoryModel repository = repositoryProvider.getRepositoryByAlias(REPOSITORY.getAlias());

        // Check
        assertThat(REPOSITORY.getInternalId(), equalTo(repository.getInternalId()));
        assertThat(REPOSITORY.getAlias(), equalTo(repository.getAlias()));
    }

    @Test
    public void getUnknownRepository() {
        RepositoryModel repository = repositoryProvider.getRepositoryByAlias(UUID.randomUUID().toString());
        assertThat(repository, is(nullValue()));
    }

    @Test
    public void getRepositories() {
        // Create repositories
        repositoryProvider.addRepository(REPOSITORY);
        REPOSITORY.setAlias("Alias2");
        repositoryProvider.addRepository(REPOSITORY);

        List<RepositoryModel> repositories = repositoryProvider.getRepositories();

        assertThat(repositories, is(notNullValue()));
        assertThat(repositories.size(), equalTo(2));
    }

    @Test
    public void removeRepository() {
        // Create REPOSITORY
        repositoryProvider.addRepository(REPOSITORY);

        boolean result = repositoryProvider.removeRepositoryByAlias(REPOSITORY.getAlias());
        REPOSITORY = repositoryProvider.getRepositoryByAlias(REPOSITORY.getAlias());

        assertThat(result, equalTo(true));
        assertThat(REPOSITORY, is(nullValue()));
    }

    @Test
    public void updateRepository() {
        // Create REPOSITORY
        repositoryProvider.addRepository(REPOSITORY);

        // Update repository
        REPOSITORY.setAlias("Alias2");
        REPOSITORY.setEnabled(false);
        REPOSITORY.setConfig(new HashMap<String, String>() {{
            put("key11", "value11");
        }});
        repositoryProvider.updateRepository(REPOSITORY);

        REPOSITORY = repositoryProvider.getRepositoryByAlias(REPOSITORY.getAlias());

        // Check
        assertThat(REPOSITORY.getAlias(), equalTo("Alias2"));
        assertThat(REPOSITORY.isEnabled(), equalTo(false));
        assertThat(REPOSITORY.getConfig().size(), equalTo(1));
        assertThat(REPOSITORY.getConfig().get("key11"), equalTo("value11"));
    }

}