package org.openfact.testsuite.repository;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;
import org.openfact.connections.jpa.PersistenceEntityProducer;
import org.openfact.connections.jpa.PersistenceExceptionConverter;
import org.openfact.models.ModelDuplicateException;
import org.openfact.models.ModelException;
import org.openfact.models.RepositoryModel;
import org.openfact.models.RepositoryProvider;
import org.openfact.models.jpa.JpaModel;
import org.openfact.models.jpa.JpaRepositoryProvider;
import org.openfact.models.jpa.entities.RepositoryEntity;

@RunWith(Arquillian.class)
@UsingDataSet("empty.xml")
public abstract class AbstractRepositoryTest {

    public static WebArchive buildArchive() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(AbstractRepositoryTest.class)
                .addAsResource("persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("client_secret.json", "META-INF/client_secret.json")
                .addClass(PersistenceEntityProducer.class)
                .addClass(PersistenceExceptionConverter.class)
                .addClass(ModelException.class)
                .addClass(ModelDuplicateException.class)
                .addClass(JpaModel.class)
                .addPackage(RepositoryEntity.class.getPackage())

                .addClass(RepositoryModel.class)
                .addClass(RepositoryProvider.class)
                .addClass(JpaRepositoryProvider.class);
    }
}
