package org.clarksnut.repositories.user.gmail;

import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class GmailQueryTest {

    @Test
    public void periodTest() {
        String query = GmailQuery.builder()
                .after(LocalDate.of(2004, 4, 16))
                .before(LocalDate.of(2017, 6, 18))
                .build()
                .query();

        assertThat(query).isNotNull()
                .matches(u -> u.equals("after:2004/04/16 before:2017/06/18"));
    }

    @Test
    public void filenameTest() {
        String query = GmailQuery.builder()
                .filename("homework.txt")
                .build()
                .query();
        assertThat(query).isNotNull()
                .matches(u -> u.equals("filename: homework.txt"));

        query = GmailQuery.builder()
                .fileType("pdf")
                .build()
                .query();
        assertThat(query).isNotNull()
                .matches(u -> u.equals("filename:pdf"));
    }

    @Test
    public void hasTest() {
        String query = GmailQuery.builder()
                .has("youtube")
                .build()
                .query();
        assertThat(query).isNotNull()
                .matches(u -> u.equals("has:youtube"));
    }

    @Test
    public void allTest() {
        String query = GmailQuery.builder()
                .after(LocalDate.of(2004, 4, 16))
                .before(LocalDate.of(2017, 6, 18))
                .filename("homework.txt")
                .has("youtube")
                .build()
                .query();

        assertThat(query).isNotNull()
                .matches(u -> u.equals("after:2004/04/16 before:2017/06/18 has:youtube filename: homework.txt"));
    }

}
