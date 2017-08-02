package org.openfact.services.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

@Singleton
@Startup
public class ESBootstrap {

    private static final Logger logger = Logger.getLogger(ESBootstrap.class);

    private TransportClient client;

    @Lock(value = LockType.READ)
    public TransportClient client() {
        return client;
    }

    @PostConstruct
    public void init() throws UnknownHostException {
        Properties config = new Properties();
        try (InputStream is = new FileInputStream("elasticsearch.properties")) {
            config.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read elasticsearch.properties", e);
        }

        logger.info("------------------------------------------------------------");
        logger.info("Starting elasticsearch.");
        logger.info("   Host:      " + config.getProperty("cluster.host"));
        logger.info("   Port: " + config.getProperty("cluster.port"));
        logger.info("   Name:       " + config.getProperty("cluster.name"));
        logger.info("------------------------------------------------------------");

        Settings settings = Settings.builder()
                .put("cluster.name", config.getProperty("cluster.name"))
                .put("client.transport.sniff", config.getProperty("client.transport.sniff"))
                .put("client.transport.ignore_cluster_name", config.getProperty("client.transport.ignore_cluster_name"))
                .put("client.transport.ping_timeout", config.getProperty("client.transport.ping_timeout"))
                .put("client.transport.nodes_sampler_interval", config.getProperty("client.transport.nodes_sampler_interval"))
                .build();

        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(
                        new InetSocketTransportAddress(
                                InetAddress.getByName(config.getProperty("cluster.host")),
                                Integer.parseInt(config.getProperty("cluster.port")))
                );

        logger.info("-----------------------------");
        logger.info("Elasticsearch client started!");
        logger.info("-----------------------------");
    }

    @PreDestroy
    private void close() {
        this.client.close();
    }

}
