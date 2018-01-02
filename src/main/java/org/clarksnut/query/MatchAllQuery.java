package org.clarksnut.query;

public class MatchAllQuery implements SimpleQuery {

    @Override
    public String getQueryName() {
        return "MatchAll";
    }

}
