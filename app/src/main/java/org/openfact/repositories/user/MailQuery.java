package org.openfact.repositories.user;

import java.time.LocalDateTime;

public class MailQuery {

    private LocalDateTime from;
    private LocalDateTime to;

    private MailQuery(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
    }

    public static Builder builder() {
        return new Builder();
    }

    public LocalDateTime from() {
        return from;
    }

    public LocalDateTime to() {
        return to;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (from != null) {
            sb.append(" after:" + from);
        }
        if (to != null) {
            sb.append(" before:" + to);
        }
        return sb.toString();
    }

    public static class Builder {
        private LocalDateTime from;
        private LocalDateTime to;

        public Builder from(LocalDateTime from) {
            this.from = from;
            return this;
        }

        public Builder to(LocalDateTime to) {
            this.to = to;
            return this;
        }

        public MailQuery build() {
            return new MailQuery(this);
        }
    }
}
