package org.openfact.models.broker;

import org.openfact.models.UserRepositoryModel;

import java.util.List;

public interface BrokerReader {

    List<BrokerElementModel> read(UserRepositoryModel repository, String query) throws ReadBrokerException;

}
