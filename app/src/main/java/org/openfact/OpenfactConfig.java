package org.openfact;

import org.jboss.logging.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OpenfactConfig {

    private static final Logger logger = Logger.getLogger(OpenfactConfig.class);

    private static OpenfactConfig instance = new OpenfactConfig();

    private Properties config;

    private OpenfactConfig() {
        config = new Properties();
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("openfact.properties");
            config.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load openfact.properties");
        }
    }

    public static OpenfactConfig getInstance() {
        if (instance == null) {
            instance = new OpenfactConfig();
        }
        return instance;
    }

    public String getProperty(String property) {
        return config.getProperty(property);
    }

    public String getProperty(String property, String defaultValue) {
        return config.getProperty(property, defaultValue);
    }

    public Object get(String property) {
        return config.get(property);
    }

    public Object getOrDefault(String property, Object defaultValue) {
        return config.getOrDefault(property, defaultValue);
    }

}
