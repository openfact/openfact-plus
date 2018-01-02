package org.clarksnut.truststore;

import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.StrictHostnameVerifier;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

@Stateless
public class JSSETruststoreConfigurator {

    @Inject
    @Truststore(Truststore.Type.FILE)
    private TruststoreProvider provider;

    private javax.net.ssl.SSLSocketFactory sslFactory;
    private TrustManager[] tm;
    private HostnameVerifier hostnameVerifier;

    @PostConstruct
    private void init() {
        if (provider.getTruststore() != null && provider.getPolicy() != null) {
            // Init sslFactory
            try {
                SSLContext sslctx = SSLContext.getInstance("TLS");
                sslctx.init(null, tm, null);
                sslFactory = sslctx.getSocketFactory();
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize SSLContext: ", e);
            }

            // Init tm
            TrustManagerFactory tmf;
            try {
                tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(provider.getTruststore());
                tm = tmf.getTrustManagers();
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize TrustManager: ", e);
            }

            // HostnameVerifier
            HostnameVerificationPolicy policy = provider.getPolicy();
            switch (policy) {
                case ANY:
                    hostnameVerifier = (s, sslSession) -> true;
                case WILDCARD:
                    hostnameVerifier = new BrowserCompatHostnameVerifier();
                case STRICT:
                    hostnameVerifier = new StrictHostnameVerifier();
                default:
                    throw new IllegalStateException("Unknown policy: " + policy.name());
            }
        }
    }

    public javax.net.ssl.SSLSocketFactory getSSLSocketFactory() {
        return sslFactory;
    }

    public TrustManager[] getTrustManagers() {
        return tm;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

}
