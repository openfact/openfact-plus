package org.openfact.models;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@RunWith(Arquillian.class)
public class MyTest {

    @PersistenceContext
    private EntityManager em;


    @Inject
    private PersonaProvider personaProvider;

    @Inject
    private UserProvider userProvider;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "model-test.war")
                .addPackages(true, Filters.exclude(".*Test.*"), "org.openfact")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("persistence.xml", "META-INF/persistence.xml");
    }

    @Test
    public void should_start_service() {
        PersonaModel persona = personaProvider.create();
        UserModel user1 = userProvider.addUser("carlos");
//        try {
//            userProvider.addUser("carlos");
//        } catch (ModelDuplicateException e) {
//            System.out.println(e);
//            System.out.println(e);
//        }

        System.out.println(em);

        ScrollableResultsModel<UserModel> scrollableUsers = userProvider.getScrollableUsers();
        while (scrollableUsers.next()) {
            UserModel u = scrollableUsers.get();
            System.out.println(u.getUsername());
        }

        System.out.println(persona);
    }
}