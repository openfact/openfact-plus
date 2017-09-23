package org.openfact.repositories.user;

import java.util.List;

public interface MailReader {

    List<MailUBLMessage> read(MailRepository mailRepository, MailQuery mailQuery) throws MailReadException;

}
