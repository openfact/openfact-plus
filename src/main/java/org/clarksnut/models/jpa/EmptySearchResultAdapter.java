package org.clarksnut.models.jpa;

import org.clarksnut.models.FacetModel;
import org.clarksnut.models.SearchResultModel;

import java.util.Collections;
import java.util.List;

public class EmptySearchResultAdapter<T> implements SearchResultModel<T> {

    @Override
    public List<T> getItems() {
        return Collections.emptyList();
    }

    @Override
    public int getTotalResults() {
        return 0;
    }

    @Override
    public List<FacetModel> getFacets() {
        return Collections.emptyList();
    }

}
