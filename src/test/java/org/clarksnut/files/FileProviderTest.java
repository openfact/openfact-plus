package org.clarksnut.files;

import org.arquillian.ape.rdbms.Cleanup;
import org.arquillian.ape.rdbms.CleanupStrategy;
import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.clarksnut.common.jpa.*;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.files.exceptions.FileStorageException;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserLinkedBrokerModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.clarksnut.query.Query;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.undertow.WARArchive;

import javax.inject.Inject;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class FileProviderTest {

    @Inject
    public FileProvider fileProvider;

    protected static File[] libs() {
        return Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve("org.mockito:mockito-core:2.13.0")
                .withTransitivity()
                .as(File.class);
    }

    @Deployment
    public static WARArchive createDeployment() throws Exception {
        MavenResolverSystem resolver = Maven.resolver();

        return ShrinkWrap.create(WARArchive.class)
                .addPackages(true, FileModel.class.getPackage())

                // Common
                .addClasses(CreatableEntity.class, CreatedAtListener.class, UpdatableEntity.class, UpdatedAtListener.class, JpaModel.class)

                // Models
                .addClasses(ClarksnutModelUtils.class)

                // Resources
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/basic-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("test-project-defaults.yml", "project-defaults.yml")
                .addAsResource("standalone.xml", "standalone.xml")
                .addAsWebInfResource("jboss-deployment-structure.xml", "jboss-deployment-structure.xml")

                // Dependencies
                .addAsLibraries(libs())
                .addAllDependencies();
    }

    @Test
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void createFilesTest() throws FileStorageException {
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
    public void removeFile() throws FileStorageException {
        FileModel file = fileProvider.addFile(new byte[]{0, 1, 2, 3, 4, 5}, ".xml");

        boolean result = fileProvider.removeFile(file);

        assertThat(result).isTrue();
    }

    @Test
    public void test() {
        assertThat(true).isEqualTo(true);
    }

}
