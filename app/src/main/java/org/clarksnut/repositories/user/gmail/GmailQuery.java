package org.clarksnut.repositories.user.gmail;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GmailQuery {

    private final String before;
    private final String after;
    private final String has;
    private final String filename;

    public GmailQuery(Builder builder) {
        this.after = builder.after.length() > 0 ? builder.after.toString().trim() : "";
        this.before = builder.before.length() > 0 ? builder.before.toString().trim() : "";
        this.has = builder.has.length() > 0 ? builder.has.toString().trim() : "";
        this.filename = builder.filename.length() > 0 ? builder.filename.toString().trim() : "";
    }

    public String query() {
        StringBuilder sb = new StringBuilder()
                .append(after).append(" ")
                .append(before).append(" ")
                .append(has).append(" ")
                .append(filename).append(" ");
        return sb.toString().trim();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private StringBuilder before = new StringBuilder();
        private StringBuilder after = new StringBuilder();
        private StringBuilder has = new StringBuilder();
        private StringBuilder filename = new StringBuilder();

        /**
         * @param type possible values: attachment, drive, document, spreadsheet, presentation, youtube
         */
        public Builder has(String type) {
            this.has.append("has:")
                    .append(type)
                    .append(" ");
            return this;
        }

        public Builder fileType(String fileType) {
            this.filename.append("filename:")
                    .append(fileType)
                    .append(" ");
            return this;
        }

        public Builder filename(String filename) {
            this.filename.append("filename: ")
                    .append(filename)
                    .append(" ");
            return this;
        }

        public Builder before(LocalDate before) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            this.before.append("before:").append(before.format(formatter));
            return this;
        }

        public Builder after(LocalDate after) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            this.after.append("after:").append(after.format(formatter));
            return this;
        }

        public GmailQuery build() {
            return new GmailQuery(this);
        }

    }

}
