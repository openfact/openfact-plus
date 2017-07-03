package org.openfact.testsuite.model;

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
import org.openfact.models.jpa.JpaModel;
import org.openfact.models.jpa.entities.RepositoryEntity;

@RunWith(Arquillian.class)
@UsingDataSet("empty.xml")
public abstract class AbstractModelTest {

    public static WebArchive buildArchive() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(AbstractModelTest.class)
                .addAsResource("persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addClass(PersistenceEntityProducer.class)
                .addClass(PersistenceExceptionConverter.class)
                .addClass(ModelException.class)
                .addClass(ModelDuplicateException.class)
                .addClass(JpaModel.class)
                .addPackage(RepositoryEntity.class.getPackage());
    }

}
