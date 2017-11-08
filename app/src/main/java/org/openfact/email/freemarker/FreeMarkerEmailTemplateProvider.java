package org.openfact.email.freemarker;

import org.keycloak.common.util.ObjectUtil;
import org.openfact.documents.DocumentModel;
import org.openfact.email.*;
import org.openfact.email.exceptions.EmailException;
import org.openfact.models.UserModel;
import org.openfact.theme.Theme;
import org.openfact.theme.ThemeProvider;
import org.openfact.theme.ThemeProviderType;
import org.openfact.theme.ThemeProviderType.Type;
import org.openfact.theme.beans.MessageFormatterMethod;
import org.openfact.theme.exceptions.FreeMarkerException;
import org.openfact.theme.freemarker.FreeMarkerUtil;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.*;

@Stateless
public class FreeMarkerEmailTemplateProvider implements EmailTemplateProvider {

    @Inject
    private FreeMarkerUtil freeMarker;

    @Inject
    @ThemeProviderType(type = Type.EXTENDING)
    private ThemeProvider themeProvider;

    @Inject
    private EmailSenderProvider emailSender;

    @Override
    public void send(EmailTemplateConfiguration config, DocumentModel document) throws EmailException {
        StringBuilder subject = new StringBuilder()
                .append("/")
                .append(toCamelCase(document.getType()))
                .append(" ")
                .append(document.getAssignedId());
        send(config.getUser(), config.getRecipient(), subject.toString(), "document.ftl", config.getAttributes(), config.getAttachments());
    }

    private void send(UserModel user, String recipient, String subjectKey, String template, Map<String, Object> attributes, List<EmailFileModel> attachments) throws EmailException {
        send(user, recipient, subjectKey, Collections.emptyList(), template, attributes, attachments);
    }

    private void send(UserModel user, String recipient, String subjectKey, List<Object> subjectAttributes, String template, Map<String, Object> attributes, List<EmailFileModel> attachments) throws EmailException {
        try {
            EmailTemplate email = processTemplate(user, subjectKey, subjectAttributes, template, attributes);
            if (attachments != null && !attachments.isEmpty()) {
                emailSender.send(recipient, email.getSubject(), email.getTextBody(), email.getHtmlBody());
            } else {
                emailSender.send(recipient, email.getSubject(), email.getTextBody(), email.getHtmlBody(), attachments);
            }
        } catch (EmailException e) {
            throw e;
        } catch (Exception e) {
            throw new EmailException("Failed to template email", e);
        }
    }

    private EmailTemplate processTemplate(UserModel user, String subjectKey, List<Object> subjectAttributes, String template, Map<String, Object> attributes) throws EmailException {
        try {
            Theme theme = themeProvider.getTheme(user.getEmailTheme(), Theme.Type.EMAIL);

            Locale locale = getLocale(user);
            attributes.put("locale", locale);
            Properties rb = theme.getMessages(locale);
            attributes.put("msg", new MessageFormatterMethod(locale, rb));
            String subject = new MessageFormat(rb.getProperty(subjectKey, subjectKey), locale).format(subjectAttributes.toArray());
            String textTemplate = String.format("text/%s", template);
            String textBody;
            try {
                textBody = freeMarker.processTemplate(attributes, textTemplate, theme);
            } catch (final FreeMarkerException e) {
                textBody = null;
            }
            String htmlTemplate = String.format("html/%s", template);
            String htmlBody;
            try {
                htmlBody = freeMarker.processTemplate(attributes, htmlTemplate, theme);
            } catch (final FreeMarkerException e) {
                htmlBody = null;
            }

            return new EmailTemplate(subject, textBody, htmlBody);
        } catch (Exception e) {
            throw new EmailException("Failed to template email", e);
        }
    }

    private String toCamelCase(String event) {
        StringBuilder sb = new StringBuilder();
        for (String s : event.toLowerCase().split("_")) {
            sb.append(ObjectUtil.capitalize(s));
        }
        return sb.toString();
    }

    private static Locale getLocale(UserModel user) {
        if (user.getLanguage() == null) {
            return Locale.ENGLISH;
        } else {
            return new Locale(user.getLanguage());
        }
    }

}
