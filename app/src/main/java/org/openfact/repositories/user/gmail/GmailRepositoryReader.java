package org.openfact.repositories.user.gmail;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.openfact.broker.BrokerType;
import org.openfact.models.UserRepositoryModel;
import org.openfact.repositories.user.*;
import org.openfact.services.managers.BrokerManager;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@UserRepositoryType(BrokerType.GOOGLE)
public class GmailRepositoryReader implements UserRepositoryReader {

    @Inject
    private BrokerManager brokerManager;

    @Override
    public List<UserRepositoryElementModel> read(UserRepositoryModel userRepository, UserRepositoryQuery query) throws UserRepositoryReadException {
        Gmail client = buildClient(userRepository);

        List<UserRepositoryElementModel> result;
        try {
            result = readMessages(client, userRepository, query).stream()
                    .map(f -> new UserRepositoryElementAdapter(userRepository, client, f))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UserRepositoryReadException("Could not buildEntity gmail messages", e);
        }
        return result;
    }

    private List<Message> readMessages(Gmail client, UserRepositoryModel userRepository, UserRepositoryQuery query) throws IOException {
        List<Message> result = new ArrayList<>();

        String gmailQuery = new QueryAdapter(query).query();

        // Search
        ListMessagesResponse response = client.users()
                .messages()
                .list(userRepository.getEmail())
                .setQ(gmailQuery)
                .execute();

        // Retrieve pages
        while (response.getMessages() != null) {
            result.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = client.users()
                        .messages()
                        .list(userRepository.getEmail())
                        .setQ(gmailQuery)
                        .setPageToken(pageToken)
                        .execute();
            } else {
                break;
            }
        }

        return result;
    }

    private Gmail buildClient(UserRepositoryModel userRepository) {
        String accessToken = brokerManager.getBrokerToken(userRepository.getType().getAlias().toLowerCase(), userRepository.getUser().getOfflineRefreshToken());
        return null;
    }

}
