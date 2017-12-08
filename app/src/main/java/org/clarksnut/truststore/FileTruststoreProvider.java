package org.clarksnut.truststore;

import org.jboss.logging.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

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
import java.util.Optional;

@Startup
@Singleton
@Truststore(Truststore.Type.FILE)
public class FileTruststoreProvider implements TruststoreProvider {

    private static final Logger logger = Logger.getLogger(FileTruststoreProvider.class);

    @Inject
    @ConfigurationValue("clarksnut.truststore.file.file")
    private Optional<String> clarksnutTruststoreFile;

    @Inject
    @ConfigurationValue("clarksnut.truststore.file.password")
    private Optional<String> clarksnutTruststorePassword;

    @Inject
    @ConfigurationValue("clarksnut.truststore.file.hostname-verification-policy")
    private Optional<String> clarksnutTruststoreHostnameVerificationPolicy;

    @Inject
    @ConfigurationValue("clarksnut.truststore.file.disabled")
    private Optional<Boolean> clarksnutTruststoreDisable;

    private HostnameVerificationPolicy policy;
    private KeyStore truststore;

    @PostConstruct
    private void init() {
        // if "truststore" . "file" is not configured then it is disabled
        if (!clarksnutTruststoreFile.isPresent()
                && !clarksnutTruststorePassword.isPresent()
                && !clarksnutTruststoreHostnameVerificationPolicy.isPresent()
                && !clarksnutTruststoreDisable.isPresent()) {
            return;
        }

        // if explicitly disabled
        if (clarksnutTruststoreDisable.orElse(true)) {
            return;
        }

        HostnameVerificationPolicy verificationPolicy;
        KeyStore truststore;

        clarksnutTruststoreFile.orElseThrow(() -> new RuntimeException("Attribute 'file' missing in 'truststore':'file' configuration"));

        clarksnutTruststorePassword.orElseThrow(()-> new RuntimeException("Attribute 'password' missing in 'truststore':'file' configuration"));

        try {
            truststore = loadStore(clarksnutTruststoreFile.get(), clarksnutTruststorePassword.get().toCharArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize TruststoreProviderFactory: " + new File(clarksnutTruststoreFile.get()).getAbsolutePath(), e);
        }
        if (policy == null) {
            verificationPolicy = HostnameVerificationPolicy.WILDCARD;
        } else {
            try {
                verificationPolicy = HostnameVerificationPolicy.valueOf(clarksnutTruststoreHostnameVerificationPolicy.get());
            } catch (Exception e) {
                throw new RuntimeException("Invalid value for 'hostname-verification-policy': " + policy + " (must be one of: ANY, WILDCARD, STRICT)");
            }
        }

        this.truststore = truststore;
        this.policy = verificationPolicy;
        logger.debug("File trustore provider initialized: " + new File(clarksnutTruststoreFile.get()).getAbsolutePath());
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
