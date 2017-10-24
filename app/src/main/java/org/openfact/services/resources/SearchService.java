package org.openfact.services.resources;

import org.elasticsearch.index.query.*;
import org.openfact.documents.DocumentModel;
import org.openfact.documents.DocumentProvider;
import org.openfact.models.QueryModel;
import org.openfact.models.SpaceProvider;
import org.openfact.models.UserProvider;
import org.openfact.representations.idm.DocumentQueryRepresentation;
import org.openfact.representations.idm.GenericDataRepresentation;
import org.openfact.services.ErrorResponse;
import org.openfact.services.ErrorResponseException;
import org.openfact.utils.ModelToRepresentation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Path("search")
@Consumes(MediaType.APPLICATION_JSON)
public class SearchService {

    @Context
    private UriInfo uriInfo;

    @Inject
    private SpaceProvider spaceProvider;

    @Inject
    private UserProvider userProvider;

    @Inject
    private DocumentProvider documentProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @GET
    @Path("spaces")
    public GenericDataRepresentation searchSpaces(@QueryParam("q") String filterText) {
        QueryModel.Builder queryBuilder = QueryModel.builder();

        if (filterText != null) {
            queryBuilder.filterText(filterText);
        }

        return new GenericDataRepresentation(spaceProvider.getSpaces(queryBuilder.build()).stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList()));
    }

    @GET
    @Path("users")
    public GenericDataRepresentation searchUsers(@QueryParam("q") String filterText) {
        QueryModel.Builder queryBuilder = QueryModel.builder();

        if (filterText != null) {
            queryBuilder.filterText(filterText);
        }

        return new GenericDataRepresentation(userProvider.getUsers(queryBuilder.build()).stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList()));
    }

    @GET
    @Path("documents")
    public Response searchDocuments(@QueryParam("q") String q) throws ErrorResponseException {
        DocumentQueryRepresentation query;
        try {
            query = new DocumentQueryRepresentation(q);
        } catch (ParseException e) {
            return ErrorResponse.error("Bad query request", Response.Status.BAD_REQUEST);
        }

        QueryBuilder filterTextQuery;
        if (query.getFilterText() != null && !query.getFilterText().trim().isEmpty() && !query.getFilterText().trim().equals("*")) {
            filterTextQuery = QueryBuilders.multiMatchQuery(query.getFilterText(),
                    DocumentModel.ASSIGNED_ID,
                    DocumentModel.SUPPLIER_NAME,
                    DocumentModel.CUSTOMER_NAME,
                    DocumentModel.SUPPLIER_ASSIGNED_ID,
                    DocumentModel.CUSTOMER_ASSIGNED_ID
            );
        } else {
            filterTextQuery = QueryBuilders.matchAllQuery();
        }

        QueryBuilder typeQuery = null;
        if (query.getTypes() != null && !query.getTypes().isEmpty()) {
            typeQuery = QueryBuilders.termsQuery(DocumentModel.TYPE, query.getTypes());
        }

        QueryBuilder currencyQuery = null;
        if (query.getCurrencies() != null && !query.getCurrencies().isEmpty()) {
            currencyQuery = QueryBuilders.termsQuery(DocumentModel.CURRENCY, query.getCurrencies());
        }

        QueryBuilder tagSQuery = null;
        if (query.getTags() != null && !query.getTags().isEmpty()) {
            tagSQuery = QueryBuilders.termsQuery(DocumentModel.TAGS, query.getTags());
        }

        RangeQueryBuilder amountQuery = null;
        if (query.getGreaterThan() != null || query.getLessThan() != null) {
            amountQuery = QueryBuilders.rangeQuery(DocumentModel.AMOUNT);
            if (query.getGreaterThan() != null) {
                amountQuery.gte(query.getGreaterThan());
            }
            if (query.getLessThan() != null) {
                amountQuery.lte(query.getLessThan());
            }
        }

        RangeQueryBuilder issueDateQuery = null;
        if (query.getAfter() != null || query.getBefore() != null) {
            issueDateQuery = QueryBuilders.rangeQuery(DocumentModel.ISSUE_DATE);
            if (query.getAfter() != null) {
                issueDateQuery.gte(query.getAfter());
            }
            if (query.getBefore() != null) {
                issueDateQuery.lte(query.getBefore());
            }
        }


        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(filterTextQuery);
        if (typeQuery != null) {
            boolQueryBuilder.filter(typeQuery);
        }
        if (currencyQuery != null) {
            boolQueryBuilder.filter(currencyQuery);
        }
        if (tagSQuery != null) {
            boolQueryBuilder.filter(tagSQuery);
        }
        if (amountQuery != null) {
            boolQueryBuilder.filter(amountQuery);
        }
        if (issueDateQuery != null) {
            boolQueryBuilder.filter(issueDateQuery);
        }

        List<DocumentModel> documents = documentProvider.getDocuments("{\"query\":" + boolQueryBuilder.toString() + "}", true);
        return Response.ok(new GenericDataRepresentation(documents.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList()))).build();
    }

}
