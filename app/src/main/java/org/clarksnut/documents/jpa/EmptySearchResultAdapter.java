package org.clarksnut.documents.jpa;

import org.clarksnut.documents.SearchResultModel;

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

}
