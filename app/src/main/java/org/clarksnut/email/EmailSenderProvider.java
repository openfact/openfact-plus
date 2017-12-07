package org.clarksnut.email;

import org.clarksnut.email.exceptions.EmailException;

import java.util.Set;

public interface EmailSenderProvider {

    void send(Set<String> recipients, String subject, String textBody, String htmlBody) throws EmailException;

    void send(Set<String> recipients, String subject, String textBody, String htmlBody, Set<EmailFileModel> attachments) throws EmailException;

}
