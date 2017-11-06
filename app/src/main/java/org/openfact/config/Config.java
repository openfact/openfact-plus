package org.openfact.config;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class Config {

    private OpenfactConfig openfactConfig;

    /**
     * Report config
     */
    @Inject
    @ConfigurationValue("openfact.report.default")
    private String defaultReportTheme;

    @Inject
    @ConfigurationValue("openfact.report.cacheTemplates")
    private Boolean reportCacheTemplates;

    @Inject
    @ConfigurationValue("openfact.report.cacheReports")
    private Boolean reportCacheReports;

    @Inject
    @ConfigurationValue("openfact.report.folder.dir")
    private String reportFolderDir;

    /**
     * Theme config
     */
    @Inject
    @ConfigurationValue("openfact.theme.default")
    private String defaultTheme;

    @Inject
    @ConfigurationValue("openfact.theme.staticMaxAge")
    private Long themeStaticMaxAge;

    @Inject
    @ConfigurationValue("openfact.theme.cacheTemplates")
    private Boolean themeCacheTemplates;

    @Inject
    @ConfigurationValue("openfact.theme.cacheThemes")
    private Boolean themeCacheReports;

    @Inject
    @ConfigurationValue("openfact.theme.folder.dir")
    private String themeFolderDir;

    /**
     * Mail config
     */
    @Inject
    @ConfigurationValue("openfact.mail.smtp.host")
    private String smtpHost;

    @Inject
    @ConfigurationValue("openfact.mail.smtp.port")
    private String smtpPort;

    @Inject
    @ConfigurationValue("openfact.mail.smtp.from")
    private String smtpFrom;

    @Inject
    @ConfigurationValue("openfact.mail.smtp.ssl")
    private Boolean smtpSsl;

    @Inject
    @ConfigurationValue("openfact.mail.smtp.starttls")
    private Boolean smtpStarttls;

    @Inject
    @ConfigurationValue("openfact.mail.smtp.auth")
    private Boolean smtpAuth;

    @Inject
    @ConfigurationValue("openfact.mail.smtp.user")
    private String smtpUser;

    @Inject
    @ConfigurationValue("openfact.mail.smtp.password")
    private String smtpPassword;

    /**
     * Gmail config
     */
    @Inject
    @ConfigurationValue("openfact.mail.vendor.gmail.applicationName")
    private String gmailApplicationName;

    @Inject
    @ConfigurationValue("openfact.mail.vendor.gmail.batchSize")
    private Integer gmailBatchSize;

    /**
     * File Storage
     */
    @Inject
    @ConfigurationValue("openfact.fileStorage.provider")
    private String fileStorageProvider;

    /**
     * Truststore
     */
    @Inject
    @ConfigurationValue("openfact.truststore.file.file")
    private String truststoreFile;

    @Inject
    @ConfigurationValue("openfact.truststore.file.password")
    private String truststoreFilePassword;

    @Inject
    @ConfigurationValue("openfact.truststore.file.hostname-verification-policy")
    private String truststoreFileHostnameVerificationPolicy;

    @Inject
    @ConfigurationValue("openfact.truststore.file.disabled")
    private Boolean truststoreFileDisabled;

    public OpenfactConfig openfact() {
        if (openfactConfig == null) {
            openfactConfig = new OpenfactConfig();
        }
        return openfactConfig;
    }

    public class OpenfactConfig {
        private ReportConfig reportConfig;
        private ThemeConfig themeConfig;
        private SmtpConfig smtpConfig;
        private FileStorage fileStorage;
        private FileTruststore fileTruststore;

        private OpenfactConfig() {
        }

        public ReportConfig report() {
            if (reportConfig == null) {
                reportConfig = new ReportConfig();
            }
            return reportConfig;
        }

        public ThemeConfig theme() {
            if (themeConfig == null) {
                themeConfig = new ThemeConfig();
            }
            return themeConfig;
        }

        public SmtpConfig smtpConfig() {
            if (smtpConfig == null) {
                smtpConfig = new SmtpConfig();
            }
            return smtpConfig;
        }

        public FileStorage fileStorage() {
            if (fileStorage == null) {
                fileStorage = new FileStorage();
            }
            return fileStorage;
        }

        public FileTruststore fileTruststore() {
            if (fileTruststore == null) {
                fileTruststore = new FileTruststore();
            }
            return fileTruststore;
        }
    }

    public class ReportConfig {
        private ReportConfig() {
        }

        public String defaultTheme() {
            return defaultReportTheme;
        }

        public boolean cacheTemplates() {
            return reportCacheTemplates != null ? reportCacheTemplates : false;
        }

        public boolean cacheReports() {
            return reportCacheReports != null ? reportCacheReports : false;
        }

        public String reportFolderDir() {
            return reportFolderDir;
        }
    }

    public class ThemeConfig {
        private ThemeConfig() {
        }

        public String defaultTheme() {
            return defaultTheme;
        }

        public Long staticMaxAge() {
            return themeStaticMaxAge;
        }

        public boolean cacheTemplates() {
            return themeCacheTemplates != null ? themeCacheTemplates : false;
        }

        public boolean cacheReports() {
            return themeCacheReports != null ? themeCacheReports : false;
        }

        public String reportFolderDir() {
            return themeFolderDir;
        }
    }

    public class SmtpConfig {
        private SmtpConfig() {
        }

        public String host() {
            return smtpHost;
        }

        public String port() {
            return smtpPort;
        }

        public String from() {
            return smtpFrom;
        }

        public boolean ssl() {
            return smtpSsl != null ? smtpSsl : false;
        }

        public boolean starttls() {
            return smtpStarttls != null ? smtpStarttls : false;
        }

        public boolean auth() {
            return smtpAuth != null ? smtpAuth : false;
        }

        public String user() {
            return smtpUser;
        }

        public String password() {
            return smtpPassword;
        }
    }

    public class GmailConfig {
        private GmailConfig() {
        }

        public String applicationName() {
            return gmailApplicationName;
        }

        public int batchSize() {
            return gmailBatchSize != null ? gmailBatchSize : 1000;
        }
    }

    public class FileStorage {
        private FileStorage() {
        }

        public String provider() {
            return fileStorageProvider;
        }
    }

    public class FileTruststore {
        private FileTruststore() {
        }

        public String file() {
            return truststoreFile;
        }

        public String password() {
            return truststoreFilePassword;
        }

        public String hostnameVerificationPolicy() {
            return truststoreFileHostnameVerificationPolicy;
        }

        public boolean disabled() {
            return truststoreFileDisabled != null ? truststoreFileDisabled : false;
        }
    }

}
