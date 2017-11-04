package org.openfact.email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailTemplateConfiguration {

    private EmailUserModel user;
    private List<EmailFileModel> attachments;
    private final Map<String, Object> attributes = new HashMap<>();

    public EmailTemplateConfiguration(EmailUserModel user) {
        this.user = user;
    }

    public EmailTemplateConfiguration setAttribute(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    public EmailTemplateConfiguration setAttachments(List<EmailFileModel> attachments) {
        this.attachments = attachments;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public EmailUserModel getUser() {
        return user;
    }

    public List<EmailFileModel> getAttachments() {
        return attachments;
    }

}
