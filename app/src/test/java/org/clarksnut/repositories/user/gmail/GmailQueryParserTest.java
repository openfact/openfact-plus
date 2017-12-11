package org.clarksnut.repositories.user.gmail;

import org.clarksnut.repositories.user.MailQuery;
import org.clarksnut.repositories.user.MailQueryParser;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class GmailQueryParserTest {

    @Test
    public void parseTest() {
        MailQuery query = MailQuery.builder()
                .after(LocalDateTime.of(LocalDate.of(2004, 4, 16), LocalTime.of(8, 15)))
                .before(LocalDateTime.of(LocalDate.of(2007, 6, 18), LocalTime.of(10, 20)))
                .fileType("pdf")
                .has("youtube")
                .build();

        MailQueryParser parser = new GmailQueryParser();
        String parsedQuery = parser.parse(query);

        assertThat(parsedQuery).isNotNull()
                .matches(u -> u.equals("after:2004/04/16 before:2007/06/18 has:youtube filename:pdf"));
    }

}
