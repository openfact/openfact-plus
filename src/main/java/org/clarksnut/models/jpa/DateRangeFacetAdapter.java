package org.clarksnut.models.jpa;

import org.clarksnut.models.FacetModel;
import org.clarksnut.models.RangeModel;
import org.hibernate.search.query.facet.Facet;

import java.util.Date;

public class DateRangeFacetAdapter implements FacetModel<RangeModel<Date>> {

    private final Facet facet;

    public DateRangeFacetAdapter(Facet facet) {
        this.facet = facet;
    }

    @Override
    public RangeModel<Date> getValue() {
        String[] split = facet.getValue()
                .replaceAll("\"", "")
                .replaceAll("[\\[\\]()]", "")
                .split(",");

        return new RangeModel<Date>() {
            @Override
            public Date getFrom() {
                return null;
            }

            @Override
            public Date getTo() {
                return null;
            }
        };
    }

    @Override
    public int getCount() {
        return facet.getCount();
    }
}
