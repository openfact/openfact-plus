package org.clarksnut.truststore;

import org.clarksnut.config.FileTruststoreConfig;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

@Startup
@Singleton
@Truststore(Truststore.Type.FILE)
public class FileTruststoreProvider implements TruststoreProvider {

    private static final Logger logger = Logger.getLogger(FileTruststoreProvider.class);

    @Inject
    private FileTruststoreConfig config;

    private HostnameVerificationPolicy policy;
    private KeyStore truststore;

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
