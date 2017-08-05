package org.openfact.connections.jpa.factories;


import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;
import java.sql.Connection;

@Stateless
public class ConnectionProducer {

    @Resource
    private DataSource ds;

    @Produces
    public Connection create() {
        try {
            return ds.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

}
