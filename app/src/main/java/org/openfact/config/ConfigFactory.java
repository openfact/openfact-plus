package org.openfact.config;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
public class ConfigFactory {

    @Produces
    public FileTruststoreConfig produceFileTruststoreConfig(@ConfigurationValue("openfact.truststore.file.file") String file,
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

    @Produces
    public GmailClientConfig produceGmailClientConfig(@ConfigurationValue("openfact.mail.vendor.gmail.applicationName") String applicationName) {
        return GmailClientConfig.builder().applicationName(applicationName).build();
    }

    @Produces
    public ReportThemeConfig produceReportThemeConfig(@ConfigurationValue("openfact.report.default") String defaultTheme,
                                                      @ConfigurationValue("openfact.report.cacheTemplates") Boolean cacheTemplates,
                                                      @ConfigurationValue("openfact.report.cacheReports") Boolean cacheReports,
                                                      @ConfigurationValue("openfact.report.folder.dir") String folderDir) {
        return ReportThemeConfig.builder()
                .defaultTheme(defaultTheme)
                .cacheTemplates(cacheTemplates)
                .cacheReports(cacheReports)
                .folderDir(folderDir)
                .build();
    }


    @Produces
    public ThemeConfig produceThemeConfig(@ConfigurationValue("openfact.theme.default") String defaultTheme,
                                          @ConfigurationValue("openfact.theme.staticMaxAge") Long staticMaxAge,
                                          @ConfigurationValue("openfact.theme.cacheTemplates") Boolean cacheTemplates,
                                          @ConfigurationValue("openfact.theme.cacheThemes") Boolean cacheThemes,
                                          @ConfigurationValue("openfact.theme.folder.dir") String folderDir) {
        return ThemeConfig.builder()
                .defaultTheme(defaultTheme)
                .staticMaxAge(staticMaxAge)
                .cacheTemplates(cacheTemplates)
                .cacheThemes(cacheThemes)
                .folderDir(folderDir)
                .build();
    }

    @Produces
    public SmtpConfig produceSmtpConfig(@ConfigurationValue("openfact.mail.smtp.host") String host,
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
        return SmtpConfig.builder()
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

}
