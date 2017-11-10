package org.openfact.services.resources;

import org.jboss.logging.Logger;
import org.keycloak.representations.AccessToken;
import org.openfact.documents.DocumentModel;
import org.openfact.documents.DocumentProvider;
import org.openfact.email.EmailTemplateProvider;
import org.openfact.email.exceptions.EmailException;
import org.openfact.models.UserModel;
import org.openfact.models.UserProvider;
import org.openfact.representations.idm.MailRepresentation;
import org.openfact.services.util.SSOContext;

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
