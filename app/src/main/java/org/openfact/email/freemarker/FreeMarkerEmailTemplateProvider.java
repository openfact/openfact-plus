package org.openfact.email.freemarker;

import org.keycloak.common.util.ObjectUtil;
import org.openfact.documents.DocumentModel;
import org.openfact.email.EmailException;
import org.openfact.email.EmailSenderProvider;
import org.openfact.email.EmailTemplateConfiguration;
import org.openfact.email.EmailTemplateProvider;
import org.openfact.theme.freemarker.FreeMarkerUtil;
import org.openfact.theme.ThemeProvider;
import org.openfact.theme.ThemeProviderType;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class FreeMarkerEmailTemplateProvider implements EmailTemplateProvider {

    @Inject
    private FreeMarkerUtil freeMarker;

    @Inject
    @ThemeProviderType(type = ThemeProviderType.Type.EXTENDING)
    private ThemeProvider themeProvider;

    @Inject
    private EmailSenderProvider emailSender;

    @Override
    public void send(EmailTemplateConfiguration config, String subjectKey, String template, Map<String, Object> attributes) throws EmailException {
        send(config, subjectKey, Collections.emptyList(), template, attributes);
    }

    private void send(EmailTemplateConfiguration config, String subjectKey, List<Object> subjectAttributes, String template, Map<String, Object> attributes) throws EmailException {
        try {

        } catch (Exception e) {
            throw new EmailException("Failed to template email", e);
        }
    }

    private void send(EmailTemplateConfiguration config, String subject, String textBody, String htmlBody) throws EmailException {
        if (config.getAttachments() == null || config.getAttachments().isEmpty()) {
            emailSender.send(config.getUser(), subject, textBody, htmlBody);
        } else {
            emailSender.send(config.getUser(), subject, textBody, htmlBody, config.getAttachments());
        }
    }

    private String toCamelCase(String event) {
        StringBuilder sb = new StringBuilder();
        for (String s : event.toLowerCase().split("_")) {
            sb.append(ObjectUtil.capitalize(s));
        }
        return sb.toString();
    }

    @Override
    public void sendDocument(EmailTemplateConfiguration config, DocumentModel document) throws EmailException {
        Map<String, Object> attributes = new HashMap<>();

        StringBuilder subject = new StringBuilder();
        subject.append("/").append(toCamelCase(document.getType())).append(" ").append(document.getAssignedId());
        send(config, subject.toString(), "document.ftl", attributes);
    }

}
