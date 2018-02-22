package org.clarksnut.models;

import java.util.List;

public interface SearchResultModel<T> {

    List<T> getItems();

    int getTotalResults();

}
