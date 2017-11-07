package org.openfact.config;

public class FileTruststoreConfig {

    private final String file;
    private final String password;
    private final String hostnameVerificationPolicy;
    private final Boolean disabled;

    private FileTruststoreConfig(Builder builder) {
        this.file = builder.file;
        this.password = builder.password;
        this.hostnameVerificationPolicy = builder.hostnameVerificationPolicy;
        this.disabled = builder.disabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getFile() {
        return file;
    }

    public String getPassword() {
        return password;
    }

    public String getHostnameVerificationPolicy() {
        return hostnameVerificationPolicy;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public static class Builder {
        private String file;
        private String password;
        private String hostnameVerificationPolicy;
        private Boolean disabled;

        public Builder file(String file) {
            this.file = file;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder hostnameVerificationPolicy(String hostnameVerificationPolicy) {
            this.hostnameVerificationPolicy = hostnameVerificationPolicy;
            return this;
        }

        public Builder disabled(Boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public FileTruststoreConfig build() {
            return new FileTruststoreConfig(this);
        }
    }

}
