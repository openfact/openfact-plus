package org.clarksnut.repositories.user;

import java.util.List;

public interface MailProvider {

    List<MailUblMessageModel> getUblMessages(MailRepositoryModel repository, MailQuery query) throws MailReadException;

}
