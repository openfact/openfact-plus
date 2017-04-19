package org.openfact.services.resources.admin;

import org.openfact.models.Constants;
import org.openfact.models.DocumentModel;
import org.openfact.models.DocumentProvider;
import org.openfact.models.DocumentQuery;
import org.openfact.models.search.SearchCriteriaFilterOperator;
import org.openfact.models.utils.ModelToRepresentation;
import org.openfact.models.utils.RepresentationToModel;
import org.openfact.representations.idm.DocumentRepresentation;
import org.openfact.services.ModelErrorResponseException;
import org.openfact.services.managers.GmailManager;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
@Path("/admin/clients")
public class ClientsAdminResource {

    @Inject
    private DocumentProvider documentProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @Inject
    private RepresentationToModel representationToModel;

    @Inject
    private GmailManager gmailManager;

    @GET
    @Path("/{assignedAccountId}/documents/sended")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DocumentRepresentation> getSendedDocuments(@PathParam("assignedAccountId") String assignedAccountId,
                                                           @QueryParam("documentId") String documentId,
                                                           @QueryParam("filterText") String filterText,
                                                           @QueryParam("documentType") String documentType,
                                                           @QueryParam("first") Integer firstResult,
                                                           @QueryParam("max") Integer maxResults) {
        List<DocumentModel> documentModels = getDocuments(assignedAccountId, documentId, filterText, documentType, firstResult, maxResults, true);
        return documentModels.stream()
                .map(f -> modelToRepresentation.toRepresentation(f))
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{assignedAccountId}/documents/received")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DocumentRepresentation> getReceivedDocuments(@PathParam("assignedAccountId") String assignedAccountId,
                                                             @QueryParam("documentId") String documentId,
                                                             @QueryParam("filterText") String filterText,
                                                             @QueryParam("documentType") String documentType,
                                                             @QueryParam("first") Integer firstResult,
                                                             @QueryParam("max") Integer maxResults) {
        List<DocumentModel> documentModels = getDocuments(assignedAccountId, documentId, filterText, documentType, firstResult, maxResults, false);
        return documentModels.stream()
                .map(f -> modelToRepresentation.toRepresentation(f))
                .collect(Collectors.toList());
    }

    private List<DocumentModel> getDocuments(String assignedAccountId,
                                             String documentId,
                                             String filterText,
                                             String documentType,
                                             Integer firstResult,
                                             Integer maxResults,
                                             boolean isSupplier) {
        firstResult = firstResult != null ? firstResult : -1;
        maxResults = maxResults != null ? maxResults : Constants.DEFAULT_MAX_RESULTS;

        List<DocumentModel> documentModels;
        if (filterText != null && !filterText.trim().isEmpty()) {
            DocumentQuery query = isSupplier ? documentProvider.createQueryByCustomer(assignedAccountId) : documentProvider.createQueryByCustomer(assignedAccountId);
            documentModels = query
                    .filterText(filterText).entityQuery()
                    .resultList().firstResult(firstResult).maxResults(maxResults).getResultList();
        } else if (documentId != null || documentType != null) {
            DocumentQuery query = isSupplier ? documentProvider.createQueryByCustomer(assignedAccountId) : documentProvider.createQueryByCustomer(assignedAccountId);
            if (documentId != null) {
                query.addFilter(DocumentModel.DOCUMENT_ID, documentId, SearchCriteriaFilterOperator.eq);
            }
            if (documentType != null) {
                query.addFilter(DocumentModel.DOCUMENT_TYPE, documentType, SearchCriteriaFilterOperator.eq);
            }
            documentModels = query.entityQuery()
                    .resultList().firstResult(firstResult).maxResults(maxResults).getResultList();
        } else {
            documentModels = documentProvider.getDocumentsBySupplier(assignedAccountId, firstResult, maxResults);
        }
        return documentModels;
    }

    @GET
    @Path("/{assignedAccountId}/documents/sended/{documentIdPk}")
    public DocumentRepresentation getSendedDocument(@PathParam("assignedAccountId") String assignedAccountId,
                                                    @PathParam("documentIdPk") String documentIdPk) {
        DocumentModel document = documentProvider.getDocumentById(documentIdPk);
        if (document == null) {
            throw new NotFoundException("Document not found.");
        }

        if (!document.getSupplierAssignedAccountId().equals(assignedAccountId)) {
            throw new ForbiddenException("Forbidden");
        }

        return modelToRepresentation.toRepresentation(document);
    }

    @GET
    @Path("/{assignedAccountId}/documents/received/{documentIdPk}")
    public DocumentRepresentation getReceivedDocument(@PathParam("assignedAccountId") String assignedAccountId,
                                                      @PathParam("documentIdPk") String documentIdPk) {
        DocumentModel document = documentProvider.getDocumentById(documentIdPk);
        if (document == null) {
            throw new NotFoundException("Document not found.");
        }

        if (!document.getCustomerAssignedAccountId().equals(assignedAccountId)) {
            throw new ForbiddenException("Forbidden");
        }

        return modelToRepresentation.toRepresentation(document);
    }

    @GET
    @Path("/{assignedAccountId}/documents/sended/{documentIdPk}/xml")
    public Response getSendedDocumentXml(@PathParam("assignedAccountId") String assignedAccountId,
                                         @PathParam("documentIdPk") String documentIdPk) throws ModelErrorResponseException {
        DocumentModel document = documentProvider.getDocumentById(documentIdPk);
        if (document == null) {
            throw new NotFoundException("Document not found.");
        }

        if (!document.getSupplierAssignedAccountId().equals(assignedAccountId)) {
            throw new ForbiddenException("Forbidden");
        }

        Optional<byte[]> result = gmailManager.getXmlFromMessage(document.getOriginUuid());
        byte[] file = result.orElseThrow(() -> new ModelErrorResponseException("Could not find Xml attachment"));
        Response.ResponseBuilder response = Response.ok(file);
        response.type("application/xml");
        response.header("content-disposition", "attachment; filename=\"" + document.getDocumentId() + ".xml\"");
        return response.build();
    }

    @GET
    @Path("/{assignedAccountId}/documents/received/{documentIdPk}/xml")
    public Response getReceivedDocumentXml(@PathParam("assignedAccountId") String assignedAccountId,
                                           @PathParam("documentIdPk") String documentIdPk) throws ModelErrorResponseException {
        DocumentModel document = documentProvider.getDocumentById(documentIdPk);
        if (document == null) {
            throw new NotFoundException("Document not found.");
        }

        if (!document.getCustomerAssignedAccountId().equals(assignedAccountId)) {
            throw new ForbiddenException("Forbidden");
        }

        Optional<byte[]> result = gmailManager.getXmlFromMessage(document.getOriginUuid());
        byte[] file = result.orElseThrow(() -> new ModelErrorResponseException("Could not find Xml attachment"));
        Response.ResponseBuilder response = Response.ok(file);
        response.type("application/xml");
        response.header("content-disposition", "attachment; filename=\"" + document.getDocumentId() + ".xml\"");
        return response.build();
    }

    @GET
    @Path("/{assignedAccountId}/documents/sended/{documentIdPk}/pdf")
    public Response getSendedDocumentPdf(@PathParam("assignedAccountId") String assignedAccountId,
                                         @PathParam("documentIdPk") String documentIdPk) throws ModelErrorResponseException {
        DocumentModel document = documentProvider.getDocumentById(documentIdPk);
        if (document == null) {
            throw new NotFoundException("Document not found.");
        }

        if (!document.getSupplierAssignedAccountId().equals(assignedAccountId)) {
            throw new ForbiddenException("Forbidden");
        }

        Optional<byte[]> result = gmailManager.getPdfFromMessage(document.getOriginUuid());
        byte[] file = result.orElseThrow(() -> new ModelErrorResponseException("Could not find Xml attachment"));
        Response.ResponseBuilder response = Response.ok(file);
        response.type("application/pdf");
        response.header("content-disposition", "attachment; filename=\"" + document.getDocumentId() + ".pdf\"");
        return response.build();
    }

    @GET
    @Path("/{assignedAccountId}/documents/received/{documentIdPk}/pdf")
    public Response getReceivedDocumentPdf(@PathParam("assignedAccountId") String assignedAccountId,
                                           @PathParam("documentIdPk") String documentIdPk) throws ModelErrorResponseException {
        DocumentModel document = documentProvider.getDocumentById(documentIdPk);
        if (document == null) {
            throw new NotFoundException("Document not found.");
        }

        if (!document.getCustomerAssignedAccountId().equals(assignedAccountId)) {
            throw new ForbiddenException("Forbidden");
        }

        Optional<byte[]> result = gmailManager.getPdfFromMessage(document.getOriginUuid());
        byte[] file = result.orElseThrow(() -> new ModelErrorResponseException("Could not find Xml attachment"));
        Response.ResponseBuilder response = Response.ok(file);
        response.type("application/pdf");
        response.header("content-disposition", "attachment; filename=\"" + document.getDocumentId() + ".pdf\"");
        return response.build();
    }

}
