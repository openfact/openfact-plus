package org.openfact.services;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.keycloak.test.builders.ClientBuilder;
import org.openfact.services.rule.JAXRSRuleTest;
import org.openfact.services.rule.SSORuleTest;

import java.io.File;
import java.io.IOException;

import static org.keycloak.test.TestsHelper.baseUrl;
import static org.keycloak.test.TestsHelper.createClient;
import static org.keycloak.test.builders.ClientBuilder.AccessType.BEARER_ONLY;

@RunWith(Arquillian.class)
public abstract class AbstractSecureTest {

    @ClassRule
    public static SSORuleTest ssoProvider = new SSORuleTest();

    @Rule
    public JAXRSRuleTest client = JAXRSRuleTest.target("http://localhost:8080");

    @Deployment(testable = false)
    public static Archive<?> createTestArchive() throws IOException {
        return ShrinkWrap.create(WebArchive.class, "arquillian-test.war")
                .addPackages(true, Filters.exclude(".*Test.*"), "org.openfact")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new StringAsset(createClient(ClientBuilder.create("arquillian-test")
                        .baseUrl(baseUrl).accessType(BEARER_ONLY))), "keycloak.json")
                .setWebXML(new File("src/main/webapp", "WEB-INF/web.xml"))
                .addAsResource(new File("src/main/resources", "META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsResource("project-defaults.yml", "project-defaults.yml");
    }

}
