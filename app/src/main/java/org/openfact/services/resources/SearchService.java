package org.openfact.services.resources;

import org.openfact.documents.DocumentModel;
import org.openfact.documents.DocumentProvider;
import org.openfact.models.QueryModel;
import org.openfact.models.SpaceProvider;
import org.openfact.models.UserProvider;
import org.openfact.representations.idm.GenericDataRepresentation;
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
    public Response searchDocuments(@QueryParam("q") String  query) throws ErrorResponseException {
//        QueryBuilders.queryStringQuery()
//        MultiMatchQueryBuilder filterTextQuery = null;
//        if (query.getFilterText() != null) {
//            filterTextQuery = QueryBuilders.multiMatchQuery(
//                    query.getFilterText(),
//                    DocumentModel.SUPPLIER_NAME,
//                    DocumentModel.CUSTOMER_NAME,
//                    DocumentModel.SUPPLIER_ASSIGNED_ID,
//                    DocumentModel.CUSTOMER_ASSIGNED_ID
//            );
//        }
//
//        TermQueryBuilder typeQuery = null;
//        if (query.getType() != null) {
//            typeQuery = QueryBuilders.termQuery(DocumentModel.TYPE, query.getType());
//        }
//
//        TermQueryBuilder currencyQuery = null;
//        if (query.getCurrency() != null) {
//            currencyQuery = QueryBuilders.termQuery(DocumentModel.CURRENCY, query.getCurrency());
//        }
//
//        RangeQueryBuilder issueDateQuery = QueryBuilders.rangeQuery(DocumentModel.ISSUE_DATE);
//        if (query.getAfter() != null || query.getBefore() != null) {
//            issueDateQuery
//                    .gte(query.getAfter())
//                    .lte(query.getBefore());
//        }
//
//        RangeQueryBuilder amountQuery = QueryBuilders.rangeQuery(DocumentModel.AMOUNT);
//        if (query.getGreater() != null || query.getLess() != null) {
//            amountQuery
//                    .gte(query.getGreater())
//                    .lte(query.getLess());
//        }
//
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
//                .must(amountQuery)
//                .must(issueDateQuery);
//        if (filterTextQuery != null) {
//            boolQueryBuilder.must(filterTextQuery);
//        }
//        if (typeQuery != null) {
//            boolQueryBuilder.must(typeQuery);
//        }
//        if (currencyQuery != null) {
//            boolQueryBuilder.must(currencyQuery);boolQueryBuilder.toString())
//        }


        List<DocumentModel> documents = documentProvider.getDocuments(query, true);
        return Response.ok(new GenericDataRepresentation(documents.stream()
                .map(f -> modelToRepresentation.toRepresentation(f, uriInfo))
                .collect(Collectors.toList()))).build();
    }

}
