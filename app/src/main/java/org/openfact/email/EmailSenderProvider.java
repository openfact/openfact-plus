package org.openfact.email;

import java.util.List;

public interface EmailSenderProvider {

    void send(EmailUserModel user, String subject, String textBody, String htmlBody) throws EmailException;

    void send(EmailUserModel user, String subject, String textBody, String htmlBody, List<EmailFileModel> attachments) throws EmailException;

}
