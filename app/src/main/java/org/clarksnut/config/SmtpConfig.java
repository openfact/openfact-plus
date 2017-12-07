package org.clarksnut.config;

public class SmtpConfig {

    private final String host;
    private final String port;
    private final String from;
    private final String fromDisplayName;
    private final String replayTo;
    private final String replayToDisplayName;
    private final String envelopeFrom;
    private final Boolean ssl;
    private final Boolean starttls;
    private final Boolean auth;
    private final String user;
    private final String password;

    private SmtpConfig(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.from = builder.from;
        this.fromDisplayName = builder.fromDisplayName;
        this.replayTo = builder.replayTo;
        this.replayToDisplayName = builder.replayToDisplayName;
        this.envelopeFrom = builder.envelopeFrom;
        this.ssl = builder.ssl;
        this.starttls = builder.starttls;
        this.auth = builder.auth;
        this.user = builder.user;
        this.password = builder.password;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getFrom() {
        return from;
    }

    public Boolean getSsl() {
        return ssl;
    }

    public Boolean getStarttls() {
        return starttls;
    }

    public Boolean getAuth() {
        return auth;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getFromDisplayName() {
        return fromDisplayName;
    }

    public String getReplayTo() {
        return replayTo;
    }

    public String getReplayToDisplayName() {
        return replayToDisplayName;
    }

    public String getEnvelopeFrom() {
        return envelopeFrom;
    }

    public static class Builder {
        private String host;
        private String port;
        private String from;
        private String fromDisplayName;
        private String replayTo;
        private String replayToDisplayName;
        private String envelopeFrom;
        private Boolean ssl;
        private Boolean starttls;
        private Boolean auth;
        private String user;
        private String password;

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(String port) {
            this.port = port;
            return this;
        }

        public Builder staticMaxAge(String port) {
            this.port = port;
            return this;
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder fromDisplayName(String fromDisplayName) {
            this.fromDisplayName = fromDisplayName;
            return this;
        }

        public Builder replayTo(String replayTo) {
            this.replayTo = replayTo;
            return this;
        }

        public Builder replayToDisplayName(String replayToDisplayName) {
            this.replayToDisplayName = replayToDisplayName;
            return this;
        }

        public Builder envelopeFrom(String envelopeFrom) {
            this.envelopeFrom = envelopeFrom;
            return this;
        }

        public Builder ssl(Boolean ssl) {
            this.ssl = ssl;
            return this;
        }

        public Builder starttls(Boolean starttls) {
            this.starttls = starttls;
            return this;
        }

        public Builder auth(Boolean auth) {
            this.auth = auth;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public SmtpConfig build() {
            return new SmtpConfig(this);
        }
    }

}
