package org.clarksnut.services.resources;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentProvider;
import org.clarksnut.email.EmailTemplateProvider;
import org.clarksnut.email.exceptions.EmailException;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.representations.idm.MailRepresentation;
import org.clarksnut.services.util.SSOContext;
import org.jboss.logging.Logger;
import org.keycloak.representations.AccessToken;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@Path("/mail")
public class MailService {

    private static final Logger logger = Logger.getLogger(MailService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private UserProvider userProvider;

    @Inject
    private DocumentProvider documentProvider;

    @Inject
    private EmailTemplateProvider emailTemplateProvider;


    private UserModel getUserByIdentityID(String identityID) {
        UserModel user = userProvider.getUserByIdentityID(identityID);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    private DocumentModel getDocumentById(String documentId) {
        DocumentModel ublDocument = documentProvider.getDocument(documentId);
        if (ublDocument == null) {
            throw new NotFoundException();
        }
        return ublDocument;
    }

    @Asynchronous
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public void sendMail(@Context final HttpServletRequest httpServletRequest,
                         MailRepresentation mailRepresentation) {
        // User
        SSOContext ssoContext = new SSOContext(httpServletRequest);
        AccessToken accessToken = ssoContext.getParsedAccessToken();
        String kcUserID = (String) accessToken.getOtherClaims().get("userID");
        UserModel user = getUserByIdentityID(kcUserID);

        // Documents
        MailRepresentation.Data data = mailRepresentation.getData();
        MailRepresentation.Attributes attributes = data.getAttributes();
        Set<DocumentModel> documents = attributes.getDocuments().stream()
                .map(this::getDocumentById)
                .collect(Collectors.toSet());

        // Send
        try {
            emailTemplateProvider.send(user, attributes.getRecipients(), documents, attributes.getSubject(), attributes.getMessage());
        } catch (EmailException e) {
            logger.error("Error sending email messages", e);
        }
    }

}
