package org.openfact.services.resources.admin;

import org.openfact.models.Constants;
import org.openfact.models.DocumentModel;
import org.openfact.models.DocumentProvider;
import org.openfact.models.DocumentQuery;
import org.openfact.models.search.SearchCriteriaFilterModel;
import org.openfact.models.search.SearchCriteriaFilterOperator;
import org.openfact.models.search.SearchCriteriaModel;
import org.openfact.models.search.SearchResultsModel;
import org.openfact.models.utils.ModelToRepresentation;
import org.openfact.models.utils.RepresentationToModel;
import org.openfact.representations.idm.DocumentRepresentation;
import org.openfact.representations.idm.search.SearchCriteriaRepresentation;
import org.openfact.representations.idm.search.SearchResultsRepresentation;
import org.openfact.services.ModelErrorResponseException;
import org.openfact.services.managers.GmailManager;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
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
    @Path("/{assignedAccountId}/documents")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DocumentRepresentation> getSendedDocuments(@PathParam("assignedAccountId") String assignedAccountId,
                                                           @QueryParam("sended") Boolean sended,
                                                           @QueryParam("received") Boolean received,
                                                           @QueryParam("documentId") String documentId,
                                                           @QueryParam("filterText") String filterText,
                                                           @QueryParam("documentType") String documentType,
                                                           @QueryParam("first") Integer firstResult,
                                                           @QueryParam("max") Integer maxResults) {
        if (sended == null && received == null) {
            sended = true;
            received = true;
        }
        if (sended != null && received != null && !sended && !received) {
            sended = true;
            received = true;
        }

        firstResult = firstResult != null ? firstResult : -1;
        maxResults = maxResults != null ? maxResults : Constants.DEFAULT_MAX_RESULTS;

        List<DocumentModel> documentModels;
        DocumentQuery query = documentProvider.createQuery();
        if (sended != null && sended) {
            DocumentQuery.SupplierQuery supplier = query.supplier(assignedAccountId);
            if (received != null && received) {
                supplier.andCustomer(assignedAccountId).buildQuery();
            } else {
                supplier.buildQuery();
            }
        } else {
            query.customer(assignedAccountId).buildQuery();
        }
        if (filterText != null && !filterText.trim().isEmpty()) {
            documentModels = query
                    .filterText(filterText).entityQuery()
                    .resultList().firstResult(firstResult).maxResults(maxResults).getResultList();
        } else if (documentId != null || documentType != null) {
            if (documentId != null) {
                query.addFilter(DocumentModel.DOCUMENT_ID, documentId, SearchCriteriaFilterOperator.eq);
            }
            if (documentType != null) {
                query.addFilter(DocumentModel.DOCUMENT_TYPE, documentType, SearchCriteriaFilterOperator.eq);
            }
            documentModels = query.entityQuery()
                    .resultList().firstResult(firstResult).maxResults(maxResults).getResultList();
        } else {
            documentModels = query.entityQuery()
                    .resultList().firstResult(firstResult).maxResults(maxResults).getResultList();
            ;
        }

        return documentModels.stream()
                .map(f -> modelToRepresentation.toRepresentation(f))
                .collect(Collectors.toList());
    }

    @POST
    @Path("/{assignedAccountId}/documents/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResultsRepresentation<DocumentRepresentation> searchDocuments(@PathParam("assignedAccountId") String assignedAccountId, SearchCriteriaRepresentation criteria) {
        String criteriaQuery = criteria.getQuery();
        SearchCriteriaModel criteriaModel = representationToModel.toModel(criteria);
        String filterText = criteria.getFilterText();

        DocumentQuery query = documentProvider.createQuery();

        // Filtertext
        if (filterText != null && !filterText.trim().isEmpty()) {
            query.filterText(filterText);
        }

        // Query
        if (criteriaQuery != null && !criteriaQuery.trim().isEmpty()) {
            if (criteriaQuery.contains("sended") && criteriaQuery.contains("received")) {
                query.supplier(assignedAccountId).orCustomer(assignedAccountId).buildQuery();
            } else if (criteriaQuery.contains("sended")) {
                query.supplier(assignedAccountId).buildQuery();
            } else if (criteriaQuery.contains("received")) {
                query.customer(assignedAccountId).buildQuery();
            }
        }

        // Filters
        if (criteriaModel.getFilters() != null && !criteriaModel.getFilters().isEmpty()) {
            for (SearchCriteriaFilterModel filter : criteriaModel.getFilters()) {
                query.addFilter(filter.getName(), filter.getValue(), filter.getOperator());
            }
        }

        // Results
        DocumentQuery.EntityQuery entityQuery = query.entityQuery();
        if (criteriaModel.getOrders() != null && !criteriaModel.getOrders().isEmpty()) {
            criteriaModel.getOrders().forEach(c -> {
                if (c.isAscending()) {
                    entityQuery.orderByAsc(c.getName());
                } else {
                    entityQuery.orderByDesc(c.getName());
                }
            });
        }

        SearchResultsModel<DocumentModel> results = entityQuery.searchResult().getSearchResult(criteriaModel.getPaging());

        SearchResultsRepresentation<DocumentRepresentation> rep = new SearchResultsRepresentation<>();
        List<DocumentRepresentation> items = new ArrayList<>();
        results.getModels().forEach(f -> items.add(modelToRepresentation.toRepresentation(f)));
        rep.setItems(items);
        rep.setTotalSize(results.getTotalSize());
        return rep;
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
