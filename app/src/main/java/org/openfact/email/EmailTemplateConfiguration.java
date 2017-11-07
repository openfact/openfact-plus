package org.openfact.email;

import org.openfact.models.UserModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailTemplateConfiguration {

    private final UserModel user;
    private final String recipient;
    private List<EmailFileModel> attachments;
    private final Map<String, Object> attributes = new HashMap<>();

    public EmailTemplateConfiguration(UserModel user, String recipient) {
        this.user = user;
        this.recipient = recipient;
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

    public UserModel getUser() {
        return user;
    }

    public List<EmailFileModel> getAttachments() {
        return attachments;
    }

    public String getRecipient() {
        return recipient;
    }
}
