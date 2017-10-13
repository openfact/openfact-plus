package org.openfact.repositories;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;
import org.openfact.repositories.user.*;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.WAR)
public class MailProviderTest {

    @Inject
    private MailProvider mailProvider;

    public void getUblMessagesTest() throws MailReadException {
        MailRepositoryModel mailRepository = MailRepositoryModel.builder()
                .email("carlosthe19916@gmail.com")
                .refreshToken("myRefreshToken")
                .build();

        MailQuery query = MailQuery.builder()
                .after(LocalDateTime.now().minusDays(5))
                .before(LocalDateTime.now())
                .fileType("xml")
                .build();

        List<MailUblMessageModel> messages = mailProvider.getUblMessages(mailRepository, query);

    }
}
