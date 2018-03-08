package org.clarksnut.models.jpa;

import org.clarksnut.models.FacetModel;
import org.clarksnut.models.RangeModel;
import org.hibernate.search.query.facet.Facet;

public class NumericRangeFacetAdapter implements FacetModel<RangeModel<Long>> {

    private final Facet facet;

    public NumericRangeFacetAdapter(Facet facet) {
        this.facet = facet;
    }

    @Override
    public RangeModel<Long> getValue() {
        String[] split = facet.getValue()
                .replaceAll("[\\[\\]()]", "")
                .split(",");
        Long from = !split[0].trim().isEmpty() ? Long.parseLong(split[0].trim()) : 0;
        Long to = !split[1].trim().isEmpty() ? Long.parseLong(split[1].trim()) : 0;

        return new RangeModel<Long>() {
            @Override
            public Long getFrom() {
                return from;
            }

            @Override
            public Long getTo() {
                return to;
            }
        };
    }

    @Override
    public int getCount() {
        return facet.getCount();
    }
}
