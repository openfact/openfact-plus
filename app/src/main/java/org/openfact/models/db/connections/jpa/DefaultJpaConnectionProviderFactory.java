package org.openfact.models.db.connections.jpa;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.Connection;

@Stateless
public class DefaultJpaConnectionProviderFactory implements JpaConnectionProviderFactory {

    @Resource
    private DataSource ds;

    @Override
    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    @Override
    public String getSchema() {
        return null;
    }

}
