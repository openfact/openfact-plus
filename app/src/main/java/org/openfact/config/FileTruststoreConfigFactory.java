package org.openfact.config;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@Dependent
public class FileTruststoreConfigFactory {

    @Produces
    public FileTruststoreConfig produce(@ConfigurationValue("openfact.truststore.file.file") String file,
                                        @ConfigurationValue("openfact.truststore.file.password") String password,
                                        @ConfigurationValue("openfact.truststore.file.hostname-verification-policy") String hostnameVerificationPolicy,
                                        @ConfigurationValue("openfact.truststore.file.disabled") Boolean disabled) {
        return FileTruststoreConfig.builder()
                .file(file)
                .password(password)
                .hostnameVerificationPolicy(hostnameVerificationPolicy)
                .disabled(disabled)
                .build();
    }

}
