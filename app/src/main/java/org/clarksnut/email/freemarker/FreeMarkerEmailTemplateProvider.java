package org.clarksnut.email.freemarker;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.email.EmailFileModel;
import org.clarksnut.email.EmailSenderProvider;
import org.clarksnut.email.EmailTemplateProvider;
import org.clarksnut.email.exceptions.EmailException;
import org.clarksnut.files.FileProvider;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.UserModel;
import org.clarksnut.theme.Theme;
import org.clarksnut.theme.ThemeProvider;
import org.clarksnut.theme.ThemeProviderType;
import org.clarksnut.theme.ThemeProviderType.Type;
import org.clarksnut.theme.beans.MessageFormatterMethod;
import org.clarksnut.theme.exceptions.FreeMarkerException;
import org.clarksnut.theme.freemarker.FreeMarkerUtil;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class FreeMarkerEmailTemplateProvider implements EmailTemplateProvider {

    private static final Logger logger = Logger.getLogger(FreeMarkerEmailTemplateProvider.class);

    @Inject
    private FreeMarkerUtil freeMarker;

    @Inject
    @ThemeProviderType(type = Type.EXTENDING)
    private ThemeProvider themeProvider;

    @Inject
    private EmailSenderProvider emailSender;

    @Inject
    private FileProvider fileProvider;

    @Override
    public void send(UserModel user, Set<String> recipients, Set<DocumentModel> documents) throws EmailException {
        send(user, recipients, documents, "", "");
    }

    @Override
    public void send(UserModel user, Set<String> recipients, Set<DocumentModel> documents, String subject, String message) throws EmailException {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("userMessage", message);

        Set<EmailFileModel> xmlFiles = documents.stream()
                .map(DocumentModel::getFileId)
                .map(fileId -> fileProvider.getFile(fileId))
                .map(fileModel -> {
                    try {
                        return new EmailFileModel(fileModel.getFile(), fileModel.getFilename(), "application/xml");
                    } catch (FileFetchException e) {
                        logger.error("Error fetching file", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        send(user, recipients, "mailDocumentSubject", Collections.singletonList(subject), "mail-documents.ftl", attributes, xmlFiles);
    }

    private void send(UserModel user, Set<String> recipients, String subjectKey, List<Object> subjectAttributes, String template, Map<String, Object> attributes, Set<EmailFileModel> attachments) throws EmailException {
        try {
            EmailTemplate email = processTemplate(user, subjectKey, subjectAttributes, template, attributes);
            if (attachments != null && !attachments.isEmpty()) {
                emailSender.send(recipients, email.getSubject(), email.getTextBody(), email.getHtmlBody());
            } else {
                emailSender.send(recipients, email.getSubject(), email.getTextBody(), email.getHtmlBody(), attachments);
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

    private static Locale getLocale(UserModel user) {
        if (user.getLanguage() == null) {
            return Locale.ENGLISH;
        } else {
            return new Locale(user.getLanguage());
        }
    }

}
