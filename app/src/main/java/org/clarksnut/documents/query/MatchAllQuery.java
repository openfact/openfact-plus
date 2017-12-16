package org.clarksnut.documents.query;

public class MatchAllQuery implements Query {

    @Override
    public String getQueryName() {
        return "MatchAll";
    }

}
