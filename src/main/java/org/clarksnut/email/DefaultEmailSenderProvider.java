package org.clarksnut.email;

import com.sun.mail.smtp.SMTPMessage;
import org.clarksnut.email.exceptions.EmailException;
import org.clarksnut.truststore.HostnameVerificationPolicy;
import org.clarksnut.truststore.JSSETruststoreConfigurator;
import org.clarksnut.truststore.Truststore;
import org.clarksnut.truststore.TruststoreProvider;
import org.jboss.logging.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.SSLSocketFactory;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Stateless
public class DefaultEmailSenderProvider implements EmailSenderProvider {

    private static final Logger logger = Logger.getLogger(DefaultEmailSenderProvider.class);

    @Inject
    @ConfigurationValue("clarksnut.mail.smtp.host")
    private Optional<String> clarksnutSmtpHost;

    @Inject
    @ConfigurationValue("clarksnut.mail.smtp.port")
    private Optional<String> clarksnutSmtpPort;

    @Inject
    @ConfigurationValue("clarksnut.mail.smtp.from")
    private Optional<String> clarksnutSmtpFrom;

    @Inject
    @ConfigurationValue("clarksnut.mail.smtp.fromDisplayName")
    private Optional<String> clarksnutSmtpFromDisplayName;

    @Inject
    @ConfigurationValue("clarksnut.mail.smtp.replyTo")
    private Optional<String> clarksnutSmtpReplyTo;

    @Inject
    @ConfigurationValue("clarksnut.mail.smtp.replyToDisplayName")
    private Optional<String> clarksnutSmtpReplyToDisplayName;

    @Inject
    @ConfigurationValue("clarksnut.mail.smtp.envelopeFrom")
    private Optional<String> clarksnutSmtpEnvelopeFrom;

    @Inject
    @ConfigurationValue("clarksnut.mail.smtp.ssl")
    private Optional<Boolean> clarksnutSmtpSsl;

    @Inject
    @ConfigurationValue("clarksnut.mail.smtp.starttls")
    private Optional<Boolean> clarksnutSmtpStarttls;

    @Inject
    @ConfigurationValue("clarksnut.mail.smtp.auth")
    private Optional<Boolean> clarksnutSmtpAuth;

    @Inject
    @ConfigurationValue("clarksnut.mail.smtp.user")
    private Optional<String> clarksnutSmtpUser;

    @Inject
    @ConfigurationValue("clarksnut.mail.smtp.password")
    private Optional<String> clarksnutSmtpPassword;

    @Inject
    private JSSETruststoreConfigurator configurator;

    @Inject
    @Truststore(Truststore.Type.FILE)
    private TruststoreProvider truststore;

    @Override
    public void send(Set<String> recipients, String subject, String textBody, String htmlBody) throws EmailException {
        send(recipients, subject, textBody, htmlBody, null);
    }

    @Override
    public void send(Set<String> addresses, String subject, String textBody, String htmlBody, Set<EmailFileModel> attachments) throws EmailException {
        if (!clarksnutSmtpFrom.isPresent() ||
                !clarksnutSmtpFromDisplayName.isPresent() ||
                !clarksnutSmtpReplyToDisplayName.isPresent()) {
            logger.error("Could not send email because invalid configuration");
            return;
        }

        Transport transport = null;
        try {
            Properties props = new Properties();
            clarksnutSmtpHost.ifPresent(s -> props.setProperty("mail.smtp.host", s));

            boolean auth = Boolean.TRUE.equals(clarksnutSmtpAuth.orElse(false));
            boolean ssl = Boolean.TRUE.equals(clarksnutSmtpSsl.orElse(false));
            boolean starttls = Boolean.TRUE.equals(clarksnutSmtpStarttls.orElse(false));

            clarksnutSmtpPort.ifPresent(s -> props.setProperty("mail.smtp.port", s));

            if (auth) {
                props.setProperty("mail.smtp.auth", "true");
            }

            if (ssl) {
                props.setProperty("mail.smtp.ssl.enable", "true");
            }

            if (starttls) {
                props.setProperty("mail.smtp.starttls.enable", "true");
            }

            if (ssl || starttls) {
                setupTruststore(props);
            }

            props.setProperty("mail.smtp.timeout", "10000");
            props.setProperty("mail.smtp.connectiontimeout", "10000");

            String from = clarksnutSmtpFrom.get();
            String fromDisplayName = clarksnutSmtpFromDisplayName.get();
            String replyToDisplayName = clarksnutSmtpReplyToDisplayName.get();

            Session session = Session.getInstance(props);

            Multipart multipart = new MimeMultipart("alternative");

            if (textBody != null) {
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(textBody, "UTF-8");
                multipart.addBodyPart(textPart);
            }

            if (htmlBody != null) {
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(htmlBody, "text/html; charset=UTF-8");
                multipart.addBodyPart(htmlPart);
            }

            if (attachments != null && attachments.size() > 0) {
                for (EmailFileModel attach : attachments) {
                    if (attach.getFile() != null) {
                        DataSource dataSource = new ByteArrayDataSource(attach.getFile(), attach.getMimeType());
                        MimeBodyPart attachPart = new MimeBodyPart();
                        attachPart.setDataHandler(new DataHandler(dataSource));
                        attachPart.setFileName(attach.getFileName());
                        multipart.addBodyPart(attachPart);
                    }
                }
            }

            SMTPMessage msg = new SMTPMessage(session);
            msg.setFrom(toInternetAddress(from, fromDisplayName));

            msg.setReplyTo(new Address[]{toInternetAddress(from, fromDisplayName)});

            if (clarksnutSmtpReplyTo.isPresent()) {
                msg.setReplyTo(new Address[]{toInternetAddress(clarksnutSmtpReplyTo.get(), replyToDisplayName)});
            }
            clarksnutSmtpEnvelopeFrom.ifPresent(msg::setEnvelopeFrom);

            InternetAddress[] internetAddresses = addresses.stream().map(address -> {
                try {
                    return new InternetAddress(address);
                } catch (AddressException e) {
                    logger.error("Could not create an internet address from:" + address);
                    return null;
                }
            }).filter(Objects::nonNull).toArray(InternetAddress[]::new);

            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(RecipientType.TO, internetAddresses);
            msg.setSubject(subject, "utf-8");
            msg.setContent(multipart);
            msg.saveChanges();
            msg.setSentDate(new Date());

            transport = session.getTransport("smtp");
            if (auth) {
                transport.connect(clarksnutSmtpUser.get(), clarksnutSmtpPassword.get());
            } else {
                transport.connect();
            }

            transport.sendMessage(msg, internetAddresses);
        } catch (Exception e) {
            logger.error("Failed to send email", e);
            throw new EmailException(e);
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    logger.warn("Failed to close transport", e);
                }
            }
        }
    }

    protected InternetAddress toInternetAddress(String email, String displayName) throws UnsupportedEncodingException, AddressException, EmailException {
        if (email == null || "".equals(email.trim())) {
            throw new EmailException("Please provide a valid address", null);
        }
        if (displayName == null || "".equals(displayName.trim())) {
            return new InternetAddress(email);
        }
        return new InternetAddress(email, displayName, "utf-8");
    }

    private void setupTruststore(Properties props) throws NoSuchAlgorithmException, KeyManagementException {
        SSLSocketFactory factory = configurator.getSSLSocketFactory();
        if (factory != null) {
            props.put("mail.smtp.ssl.socketFactory", factory);
            if (truststore.getPolicy() == HostnameVerificationPolicy.ANY) {
                props.setProperty("mail.smtp.ssl.trust", "*");
            }
        }
    }
}
