package org.openfact.representations.idm;

import java.util.Set;

public class MailRepresentation {

    private Data data;

    public MailRepresentation() {
    }

    public MailRepresentation(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String id;
        private String type;
        private Attributes attributes;

        public MailRepresentation toSpaceRepresentation() {
            return new MailRepresentation(this);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }
    }

    public static class Attributes {
        private String id;
        private String subject;
        private String message;
        private Set<String> documents;
        private Set<String> recipients;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Set<String> getDocuments() {
            return documents;
        }

        public void setDocuments(Set<String> documents) {
            this.documents = documents;
        }

        public Set<String> getRecipients() {
            return recipients;
        }

        public void setRecipients(Set<String> recipients) {
            this.recipients = recipients;
        }
    }

}
