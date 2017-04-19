package org.openfact.models.utils;

import org.openfact.models.search.SearchCriteriaFilterOperator;
import org.openfact.models.search.SearchCriteriaModel;
import org.openfact.representations.idm.search.PagingRepresentation;
import org.openfact.representations.idm.search.SearchCriteriaFilterOperatorRepresentation;
import org.openfact.representations.idm.search.SearchCriteriaFilterRepresentation;
import org.openfact.representations.idm.search.SearchCriteriaRepresentation;

import javax.ejb.Stateless;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.BiFunction;
import java.util.function.Function;

@Stateless
public class RepresentationToModel {

    public SearchCriteriaModel toModel(SearchCriteriaRepresentation rep) {
        SearchCriteriaModel model = new SearchCriteriaModel();

        // filters
        Function<SearchCriteriaFilterOperatorRepresentation, SearchCriteriaFilterOperator> operatorFunction = f -> SearchCriteriaFilterOperator.valueOf(f.toString());

        BiFunction<Object, SearchCriteriaFilterRepresentation.FilterValueType, Object> valueFunction = (value, type) -> {
            if (type == null) return value;
            Object result = null;
            switch (type) {
                case LONG:
                    result = (long) value;
                    break;
                case STRING:
                    result = (String) value;
                    break;
                case DATE:
                    result = LocalDateTime.parse((String) value, DateTimeFormatter.ISO_DATE);
                    break;
                case DATETIME:
                    result = LocalDateTime.parse((String) value, DateTimeFormatter.ISO_DATE_TIME);
                    break;
                default:
                    result = value;
                    break;
            }
            return result;
        };

        rep.getFilters().forEach(f -> {
            model.addFilter(f.getName(), valueFunction.apply(f.getValue(), f.getType()),
                    operatorFunction.apply(f.getOperator()));
        });

        // sorter
        rep.getOrders().forEach(f -> model.addOrder(f.getName(), f.isAscending()));

        // paging
        if (rep.getPaging() != null) {
            PagingRepresentation paging = rep.getPaging();
            model.setPageSize(paging.getPageSize());
            model.setPage(paging.getPage());
        }

        return model;
    }

}