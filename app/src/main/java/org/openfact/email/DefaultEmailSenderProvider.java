package org.openfact.email;

import com.sun.mail.smtp.SMTPMessage;
import org.jboss.logging.Logger;
import org.openfact.config.SmtpConfig;
import org.openfact.email.exceptions.EmailException;
import org.openfact.truststore.HostnameVerificationPolicy;
import org.openfact.truststore.JSSETruststoreConfigurator;
import org.openfact.truststore.Truststore;
import org.openfact.truststore.Truststore.Type;
import org.openfact.truststore.TruststoreProvider;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.SSLSocketFactory;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Stateless
public class DefaultEmailSenderProvider implements EmailSenderProvider {

    private static final Logger logger = Logger.getLogger(DefaultEmailSenderProvider.class);

    @Inject
    private SmtpConfig config;

    @Inject
    private JSSETruststoreConfigurator configurator;

    @Inject
    @Truststore(Type.FILE)
    private TruststoreProvider truststore;

    @Override
    public void send(String recipient, String subject, String textBody, String htmlBody) throws EmailException {
        send(recipient, subject, textBody, htmlBody, null);
    }

    @Override
    public void send(String address, String subject, String textBody, String htmlBody, List<EmailFileModel> attachments) throws EmailException {
        Transport transport = null;
        try {
            Properties props = new Properties();
            if (config.getHost() != null) {
                props.setProperty("mail.smtp.host", config.getHost());
            }

            boolean auth = Boolean.TRUE.equals(config.getAuth());
            boolean ssl = Boolean.TRUE.equals(config.getSsl());
            boolean starttls = Boolean.TRUE.equals(config.getStarttls());

            if (config.getPort() != null) {
                props.setProperty("mail.smtp.port", config.getPort());
            }

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

            String from = config.getFrom();
            String fromDisplayName = config.getFromDisplayName();
            String replyTo = config.getReplayTo();
            String replyToDisplayName = config.getReplayToDisplayName();
            String envelopeFrom = config.getEnvelopeFrom();

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
            if (replyTo != null) {
                msg.setReplyTo(new Address[]{toInternetAddress(replyTo, replyToDisplayName)});
            }
            if (envelopeFrom != null) {
                msg.setEnvelopeFrom(envelopeFrom);
            }

            msg.setFrom(new InternetAddress(from));
            msg.setHeader("To", address);
            msg.setSubject(subject, "utf-8");
            msg.setContent(multipart);
            msg.saveChanges();
            msg.setSentDate(new Date());

            transport = session.getTransport("smtp");
            if (auth) {
                transport.connect(config.getUser(), config.getPassword());
            } else {
                transport.connect();
            }
            transport.sendMessage(msg, new InternetAddress[]{new InternetAddress(address)});
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
