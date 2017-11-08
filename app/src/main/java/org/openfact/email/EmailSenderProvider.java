package org.openfact.email;

import org.openfact.email.exceptions.EmailException;

import java.util.List;

public interface EmailSenderProvider {

    void send(String recipient, String subject, String textBody, String htmlBody) throws EmailException;

    void send(String recipient, String subject, String textBody, String htmlBody, List<EmailFileModel> attachments) throws EmailException;

}
