package org.clarksnut.repositories.user;

public class MailRepositoryModel {

    private final String email;
    private final String refreshToken;

    private MailRepositoryModel(Builder builder) {
        this.email = builder.email;
        this.refreshToken = builder.refreshToken;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getEmail() {
        return email;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public static class Builder {
        private String email;
        private String refreshToken;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public MailRepositoryModel build() {
            return new MailRepositoryModel(this);
        }
    }
}
