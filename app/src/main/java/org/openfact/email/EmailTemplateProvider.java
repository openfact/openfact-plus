package org.openfact.email;

import org.openfact.documents.DocumentModel;
import org.openfact.email.exceptions.EmailException;

public interface EmailTemplateProvider {

    void send(EmailTemplateConfiguration config, DocumentModel document) throws EmailException;

}