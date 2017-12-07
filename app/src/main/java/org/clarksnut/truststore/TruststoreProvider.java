package org.clarksnut.truststore;

import java.security.KeyStore;

public interface TruststoreProvider {

    HostnameVerificationPolicy getPolicy();

    KeyStore getTruststore();

}
