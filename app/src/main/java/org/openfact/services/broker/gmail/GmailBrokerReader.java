package org.openfact.services.broker.gmail;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.openfact.models.UserRepositoryModel;
import org.openfact.models.broker.BrokerElementModel;
import org.openfact.models.broker.BrokerReader;
import org.openfact.models.broker.ReadBrokerException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class GmailBrokerReader implements BrokerReader {

    @Inject
    private GmailClientFactory clientFactory;

    @Override
    public List<BrokerElementModel> read(UserRepositoryModel userRepository, String query) throws ReadBrokerException {
        Gmail client = clientFactory.getClientService(userRepository);

        List<BrokerElementModel> result;
        try {
            result = readMessages(client, userRepository, query).stream()
                    .map(f -> new BrokerElementAdapter(userRepository, client, f))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ReadBrokerException("Could not buildEntity gmail messages", e);
        }
        return result;
    }

    private List<Message> readMessages(Gmail client, UserRepositoryModel userRepository, String query) throws IOException {
        List<Message> result = new ArrayList<>();

        // Search
        ListMessagesResponse response = client.users()
                .messages()
                .list(userRepository.getEmail())
                .setQ(query)
                .execute();

        // Retrieve pages
        while (response.getMessages() != null) {
            result.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = client.users()
                        .messages()
                        .list(userRepository.getEmail())
                        .setQ(query)
                        .setPageToken(pageToken)
                        .execute();
            } else {
                break;
            }
        }

        return result;
    }

}
