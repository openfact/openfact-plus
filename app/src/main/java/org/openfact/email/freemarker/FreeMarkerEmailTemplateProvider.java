package org.openfact.email.freemarker;

import org.jboss.logging.Logger;
import org.keycloak.common.util.ObjectUtil;
import org.openfact.documents.DocumentModel;
import org.openfact.email.EmailFileModel;
import org.openfact.email.EmailSenderProvider;
import org.openfact.email.EmailTemplateProvider;
import org.openfact.email.exceptions.EmailException;
import org.openfact.files.FileModel;
import org.openfact.files.FileProvider;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.UserModel;
import org.openfact.report.ReportTemplateProvider;
import org.openfact.services.resources.DocumentsService;
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
