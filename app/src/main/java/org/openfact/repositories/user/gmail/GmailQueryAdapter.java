package org.openfact.repositories.user.gmail;

import org.openfact.repositories.user.MailQuery;

public class GmailQueryAdapter {

    private MailQuery query;

    public GmailQueryAdapter(MailQuery query) {
        this.query = query;
    }

    public String query() {
        StringBuilder sb = new StringBuilder();
        if (query.from() != null) {
            sb.append(" after:" + query.from());
        }
        if (query.to() != null) {
            sb.append(" before:" + query.to());
        }

        return sb.toString();
    }

}
