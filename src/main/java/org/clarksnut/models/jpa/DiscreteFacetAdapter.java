package org.clarksnut.models.jpa;

import org.clarksnut.models.FacetModel;
import org.hibernate.search.query.facet.Facet;

public class DiscreteFacetAdapter implements FacetModel<String> {

    private final Facet facet;

    public DiscreteFacetAdapter(Facet facet) {
        this.facet = facet;
    }

    @Override
    public String getValue() {
        return facet.getValue();
    }

    @Override
    public int getCount() {
        return facet.getCount();
    }

}
