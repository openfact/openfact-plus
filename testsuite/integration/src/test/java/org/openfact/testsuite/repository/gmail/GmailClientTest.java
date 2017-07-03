package org.openfact.testsuite.repository.gmail;

import com.google.api.services.gmail.Gmail;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openfact.repository.gmail.GmailClient;
import org.openfact.testsuite.TestUtil;
import org.openfact.testsuite.repository.AbstractRepositoryTest;

import javax.inject.Inject;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class GmailClientTest extends AbstractRepositoryTest {

    @Inject
    private GmailClient gmailClient;

    @Deployment
    public static Archive createDeployment() {
        Archive[] mockitoLibs = TestUtil.getLibraries();
        Archive[] googleLibs = TestUtil.getGoogleLibraries();
        WebArchive archive = buildArchive()
                .addClass(GmailClient.class);
        return archive.addAsLibraries(mockitoLibs)
                .addAsLibraries(googleLibs);
    }

    @Test
    public void getClientService() throws IOException {
        Gmail gmail = gmailClient.getClientService();
        assertThat(gmail, is(notNullValue()));
    }

}
