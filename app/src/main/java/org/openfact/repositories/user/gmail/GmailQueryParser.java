package org.openfact.repositories.user.gmail;

import org.openfact.repositories.user.MailQuery;
import org.openfact.repositories.user.MailQueryParser;

public class GmailQueryParser implements MailQueryParser{

    @Override
    public String parse(MailQuery query) {
        GmailQuery.Builder builder = new GmailQuery.Builder();
        if (query.getAfter() != null) {
            builder.after(query.getAfter().toLocalDate());
        }
        if (query.getBefore() != null) {
            builder.before(query.getBefore().toLocalDate());
        }
        if (query.getHas() != null) {
            query.getHas().forEach(builder::has);
        }
        if (query.getFileType() != null) {
            query.getFileType().forEach(builder::fileType);
        }
        return builder.build().query();
    }

}
