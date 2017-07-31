package org.openfact.services.resources;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.jboss.logging.Logger;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.util.TokenUtil;
import org.openfact.models.*;
import org.openfact.models.utils.DocumentUtil;
import org.openfact.models.utils.ModelToRepresentation;
import org.openfact.representation.idm.ContextInformationRepresentation;
import org.openfact.representation.idm.ExtProfileRepresentation;
import org.openfact.services.ErrorResponse;
import org.openfact.services.util.KeycloakUtil;
import org.w3c.dom.Document;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/documents")
@Stateless
public class DocumentsService {

    private static final Logger logger = Logger.getLogger(DocumentsService.class);

    @Inject
    private DocumentUtil documentUtil;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void importDocument() {
        Document document = null;
        XContentBuilder json = documentUtil.read(document);
    }

}