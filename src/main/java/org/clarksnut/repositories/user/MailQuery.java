package org.clarksnut.repositories.user;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class MailQuery {

    private final LocalDateTime after;
    private final LocalDateTime before;
    private final Set<String> has;
    private final Set<String> fileType;

    private MailQuery(Builder builder) {
        this.after = builder.after;
        this.before = builder.before;
        this.has = builder.has;
        this.fileType = builder.fileType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public LocalDateTime getAfter() {
        return after;
    }

    public LocalDateTime getBefore() {
        return before;
    }

    public Set<String> getHas() {
        return has;
    }

    public Set<String> getFileType() {
        return fileType;
    }

    public static class Builder {
        private LocalDateTime after;
        private LocalDateTime before;
        private Set<String> has = new HashSet<>();
        private Set<String> fileType = new HashSet<>();

        public Builder after(LocalDateTime after) {
            this.after = after;
            return this;
        }

        public Builder before(LocalDateTime before) {
            this.before = before;
            return this;
        }

        public Builder has(String type) {
            this.has.add(type);
            return this;
        }

        public Builder fileType(String fileType) {
            this.fileType.add(fileType);
            return this;
        }

        public MailQuery build() {
            return new MailQuery(this);
        }
    }
}
