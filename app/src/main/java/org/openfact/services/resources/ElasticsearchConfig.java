package org.openfact.services.resources;

import org.elasticsearch.client.transport.TransportClient;

import javax.ejb.Singleton;

@Singleton
public class ElasticsearchConfig {

    private TransportClient client;

    public TransportClient getClient() {
        return client;
    }

}
