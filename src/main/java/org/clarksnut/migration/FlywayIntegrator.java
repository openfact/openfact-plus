package org.clarksnut.migration;

import org.flywaydb.core.Flyway;
import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.jboss.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class FlywayIntegrator implements Integrator {

    private static final Logger logger = Logger.getLogger(FlywayIntegrator.class);

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        logger.info("Starting Flyway Migration");

        Flyway flyway = new Flyway();
        String dataSourceJndi = getDatasourceNameJndi();
        try {
            DataSource dataSource = (DataSource) new InitialContext().lookup(dataSourceJndi);
            flyway.setDataSource(dataSource);
        } catch (NamingException ex) {
            logger.error("Error while looking up DataSource", ex);
            // Do not proceed
            return;
        }

        Dialect dialect = sessionFactory.getJdbcServices().getDialect();
        if (dialect instanceof H2Dialect) {
            flyway.setLocations("classpath:db/migration/h2");
        } else if (dialect instanceof PostgreSQL9Dialect) {
            flyway.setLocations("classpath:db/migration/postgresql");
        } else {
            throw new IllegalStateException("Not supported Dialect");
        }

        flyway.migrate();
        logger.info("Finished Flyway Migration");
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

    }

    private String getDatasourceNameJndi() {
        return "java:jboss/datasources/ClarksnutDS";
    }

}
