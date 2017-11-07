package org.openfact.config;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class SmtpConfigFactory {

    private SmtpConfig config;

    @Inject
    public SmtpConfigFactory(
            @ConfigurationValue("openfact.mail.smtp.host") String host,
            @ConfigurationValue("openfact.mail.smtp.port") String port,
            @ConfigurationValue("openfact.mail.smtp.from") String from,
            @ConfigurationValue("openfact.mail.smtp.fromDisplayName") String fromDisplayName,
            @ConfigurationValue("openfact.mail.smtp.replyTo") String replyTo,
            @ConfigurationValue("openfact.mail.smtp.replyToDisplayName") String replyToDisplayName,
            @ConfigurationValue("openfact.mail.smtp.envelopeFrom") String envelopeFrom,
            @ConfigurationValue("openfact.mail.smtp.ssl") Boolean ssl,
            @ConfigurationValue("openfact.mail.smtp.starttls") Boolean starttls,
            @ConfigurationValue("openfact.mail.smtp.auth") Boolean auth,
            @ConfigurationValue("openfact.mail.smtp.user") String user,
            @ConfigurationValue("openfact.mail.smtp.password") String password) {
        this.config = SmtpConfig.builder()
                .host(host)
                .port(port)
                .from(from)
                .fromDisplayName(fromDisplayName)
                .replayTo(replyTo)
                .replayToDisplayName(replyToDisplayName)
                .envelopeFrom(envelopeFrom)
                .ssl(ssl)
                .starttls(starttls)
                .auth(auth)
                .user(user)
                .user(password)
                .build();
    }

    @Produces
    public SmtpConfig produce() {
        return config;
    }

}
