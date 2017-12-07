package org.clarksnut.repositories.user.gmail;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import org.clarksnut.repositories.user.MailReadException;
import org.clarksnut.repositories.user.MailRepositoryModel;
import org.clarksnut.repositories.user.MailUblMessageModel;

import java.io.IOException;
import java.util.List;

public class MailUblMessageAdapter implements MailUblMessageModel {

    private final MailRepositoryModel mailRepository;
    private final Gmail client;
    private final Message message;

    public MailUblMessageAdapter(Gmail gmail, MailRepositoryModel mailRepository, Message message) {
        this.mailRepository = mailRepository;
        this.client = gmail;
        this.message = message;
    }

    @Override
    public byte[] getXml() throws MailReadException {
        try {
            return getFileByExtension(".xml", ".XML");
        } catch (IOException e) {
            throw new MailReadException("Could not retrieve xml document from gmail broker", e);
        }
    }

    @Override
    public byte[] getInvoice() throws MailReadException {
        try {
            return getFileByExtension(".pdf", ".PDF");
        } catch (IOException e) {
            throw new MailReadException("Could not retrieve pdf invoice from gmail broker", e);
        }
    }

    private byte[] getFileByExtension(String... validExtension) throws IOException {
        if (validExtension == null || validExtension.length == 0) {
            throw new IllegalStateException("Invalid extension");
        }

        List<MessagePart> parts = message.getPayload().getParts();
        for (MessagePart part : parts) {
            String filename = part.getFilename();
            if (filename != null && filename.length() > 0) {
                boolean endsWith = false;
                for (String extension : validExtension) {
                    if (filename.endsWith(extension)) {
                        endsWith = true;
                        break;
                    }
                }

                if (endsWith) {
                    String attachmentId = part.getBody().getAttachmentId();
                    MessagePartBody messagePartBody = client.users()
                            .messages()
                            .attachments()
                            .get(mailRepository.getEmail(), message.getId(), attachmentId)
                            .execute();

                    Base64 base64url = new Base64(true);
                    return base64url.decodeBase64(messagePartBody.getData());
                }
            }
        }

        return null;
    }
}
