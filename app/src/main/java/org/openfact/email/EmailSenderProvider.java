package org.openfact.email;

import org.openfact.email.exceptions.EmailException;

import java.util.List;
import java.util.Set;

public interface EmailSenderProvider {

    void send(Set<String> recipients, String subject, String textBody, String htmlBody) throws EmailException;

    void send(Set<String> recipients, String subject, String textBody, String htmlBody, Set<EmailFileModel> attachments) throws EmailException;

}
