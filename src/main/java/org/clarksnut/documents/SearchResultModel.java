package org.clarksnut.documents;

import java.util.List;

public interface SearchResultModel<T> {

    List<T> getItems();

    int getTotalResults();

}
