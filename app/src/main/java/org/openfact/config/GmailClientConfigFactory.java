package org.openfact.config;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
public class GmailClientConfigFactory {

    @Produces
    public GmailClientConfig produce(@ConfigurationValue("openfact.mail.vendor.gmail.applicationName") String applicationName) {
        return GmailClientConfig.builder().applicationName(applicationName).build();
    }

}
