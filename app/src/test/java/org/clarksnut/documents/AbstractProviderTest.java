package org.clarksnut.documents;

import org.clarksnut.common.jpa.*;
import org.clarksnut.files.FileModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserLinkedBrokerModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.clarksnut.query.Query;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.runner.RunWith;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.File;

@RunWith(Arquillian.class)
public abstract class AbstractProviderTest {

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
                .addPackages(true, DocumentModel.class.getPackage())

                // Common
                .addClasses(CreatableEntity.class, CreatedAtListener.class, UpdatableEntity.class, UpdatedAtListener.class, JpaModel.class)

                // Files
                .addClasses(FileModel.class, XmlFileModel.class, XmlUBLFileModel.class)
                .addPackage(FileFetchException.class.getPackage())

                // Models
                .addClasses(UserModel.class, UserLinkedBrokerModel.class)
                .addClasses(SpaceModel.class)

                .addClasses(ClarksnutModelUtils.class)

                // Queries
                .addPackage(Query.class.getPackage())

                // Resources
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsResource("test-project-defaults.yml", "project-defaults.yml")
                .addAsResource("standalone.xml", "standalone.xml")
                .addAsWebInfResource("jboss-deployment-structure.xml", "jboss-deployment-structure.xml")

                .addAsResource("peru/document/invoice/FF11-00000003.xml", "peru/document/invoice/FF11-00000003.xml")
                .addAsResource("peru/document/invoice/BB11-1.xml", "peru/document/invoice/BB11-1.xml")
                .addAsResource("peru/document/creditnote/FF11-3.xml", "peru/document/creditnote/FF11-3.xml")
                .addAsResource("peru/document/debitnote/FF11-5.xml", "peru/document/debitnote/FF11-5.xml")

                // Dependencies
                .addAsLibraries(libs())
                .addAllDependencies();
    }

}
