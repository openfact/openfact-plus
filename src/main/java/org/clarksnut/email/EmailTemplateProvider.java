package org.clarksnut.email;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.email.exceptions.EmailException;
import org.clarksnut.models.UserModel;

import java.util.Set;

public interface EmailTemplateProvider {

    void send(UserModel user, Set<String> recipients, Set<DocumentModel> documents) throws EmailException;

    void send(UserModel user, Set<String> recipients, Set<DocumentModel> documents, String subject, String message) throws EmailException;

}