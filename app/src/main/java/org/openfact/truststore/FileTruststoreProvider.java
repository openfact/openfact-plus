package org.openfact.truststore;

import org.jboss.logging.Logger;
import org.openfact.config.FileTruststoreConfig;
import org.openfact.truststore.Truststore.Type;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Properties;

@Startup
@Truststore(Type.FILE)
@Singleton(name = "FileTruststoreProvider")
public class FileTruststoreProvider implements TruststoreProvider {

    private static final Logger logger = Logger.getLogger(FileTruststoreProvider.class);

    private final FileTruststoreConfig config;
    private HostnameVerificationPolicy policy;
    private KeyStore truststore;

    @Inject
    public FileTruststoreProvider(FileTruststoreConfig config) {
        this.config = config;
    }

    @PostConstruct
    private void init() {
        String storepath = config.getFile();
        String pass = config.getPassword();
        String policy = config.getHostnameVerificationPolicy();
        Boolean disabled = config.getDisabled();

        // if "truststore" . "file" is not configured then it is disabled
        if (storepath == null && pass == null && policy == null && disabled == null) {
            return;
        }

        // if explicitly disabled
        if (disabled != null && disabled) {
            return;
        }

        HostnameVerificationPolicy verificationPolicy;
        KeyStore truststore;

        if (storepath == null) {
            throw new RuntimeException("Attribute 'file' missing in 'truststore':'file' configuration");
        }
        if (pass == null) {
            throw new RuntimeException("Attribute 'password' missing in 'truststore':'file' configuration");
        }

        try {
            truststore = loadStore(storepath, pass.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize TruststoreProviderFactory: " + new File(storepath).getAbsolutePath(), e);
        }
        if (policy == null) {
            verificationPolicy = HostnameVerificationPolicy.WILDCARD;
        } else {
            try {
                verificationPolicy = HostnameVerificationPolicy.valueOf(policy);
            } catch (Exception e) {
                throw new RuntimeException("Invalid value for 'hostname-verification-policy': " + policy + " (must be one of: ANY, WILDCARD, STRICT)");
            }
        }

        this.truststore = truststore;
        this.policy = verificationPolicy;
        logger.debug("File trustore provider initialized: " + new File(storepath).getAbsolutePath());
    }

    private KeyStore loadStore(String path, char[] password) throws Exception {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream is = new FileInputStream(path);
        try {
            ks.load(is, password);
            return ks;
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Lock(LockType.READ)
    @Override
    public HostnameVerificationPolicy getPolicy() {
        return policy;
    }

    @Lock(LockType.READ)
    @Override
    public KeyStore getTruststore() {
        return truststore;
    }

}
