package org.openfact.email;

import org.openfact.documents.DocumentModel;
import org.openfact.email.exceptions.EmailException;
import org.openfact.models.UserModel;

import java.util.Set;

public interface EmailTemplateProvider {

    void send(UserModel user, Set<String> recipients, Set<DocumentModel> documents) throws EmailException;

    void send(UserModel user, Set<String> recipients, Set<DocumentModel> documents, String subject, String message) throws EmailException;

}