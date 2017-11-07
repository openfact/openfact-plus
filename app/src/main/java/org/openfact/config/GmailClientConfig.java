package org.openfact.config;

public class GmailClientConfig {

    private final String applicationName;

    private GmailClientConfig(Builder builder) {
        this.applicationName = builder.applicationName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getApplicationName(String defaultValue) {
        return applicationName != null ? applicationName : defaultValue;
    }

    public static class Builder {
        private String applicationName;

        public Builder applicationName(String applicationName) {
            this.applicationName = applicationName;
            return this;
        }

        public GmailClientConfig build() {
            return new GmailClientConfig(this);
        }
    }

}
