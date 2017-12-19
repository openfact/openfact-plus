package org.clarksnut.documents.jpa;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.DocumentUserModel;
import org.clarksnut.documents.DocumentUserQueryModel;
import org.clarksnut.query.TermQuery;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.db.jpa.SpaceAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ESDocumentUserQueryParserTest {

    @Mock
    private UserModel user;

    @Test
    public void getQueryWithNullQueries() throws Exception {
        DocumentUserQueryModel query = DocumentUserQueryModel.builder().build();

        ESDocumentUserQueryParser parser = new ESDocumentUserQueryParser();

        boolean exception = false;
        try {
            parser.getQuery(null, query);
        } catch (IllegalStateException e) {
            exception = true;
        }

        assertThat(exception).isEqualTo(true);
    }

    @Test
    public void getQueryWithNotAllowedSpacesByUser() throws Exception {
        Mockito.when(user.getAllPermitedSpaces()).thenReturn(new HashSet<>());

        DocumentUserQueryModel query = DocumentUserQueryModel.builder()
                .addDocumentFilter(new TermQuery(DocumentModel.CURRENCY, "PEN"))
                .build();

        ESDocumentUserQueryParser parser = new ESDocumentUserQueryParser();
        String esQuery = parser.getQuery(user, query);

        Mockito.verify(user, Mockito.atLeastOnce()).getAllPermitedSpaces();
        assertThat(esQuery).isNull();
    }

    @Test
    public void getQueryWithNoIntersection() throws Exception {
        HashSet<SpaceModel> permittedSpaces = new HashSet<>();
        permittedSpaces.add(new MockSpaceModel("46779354"));
        permittedSpaces.add(new MockSpaceModel("10467793549"));
        permittedSpaces.add(new MockSpaceModel("123456789"));

        Mockito.when(user.getAllPermitedSpaces()).thenReturn(permittedSpaces);

        DocumentUserQueryModel query = DocumentUserQueryModel.builder()
                .addDocumentFilter(new TermQuery(DocumentModel.CURRENCY, "PEN"))
                .build();

        ESDocumentUserQueryParser parser = new ESDocumentUserQueryParser();
        String esQuery = parser.getQuery(user, query, new MockSpaceModel("00000000"));

        Mockito.verify(user, Mockito.atLeastOnce()).getAllPermitedSpaces();
        assertThat(esQuery).isNull();
    }

    @Test
    public void getQueryOnDocument() throws Exception {
        HashSet<SpaceModel> permittedSpaces = new HashSet<>();
        permittedSpaces.add(new MockSpaceModel("46779354"));
        permittedSpaces.add(new MockSpaceModel("10467793549"));
        permittedSpaces.add(new MockSpaceModel("123456789"));

        Mockito.when(user.getAllPermitedSpaces()).thenReturn(permittedSpaces);

        DocumentUserQueryModel query = DocumentUserQueryModel.builder()
                .addDocumentFilter(new TermQuery(DocumentModel.CURRENCY, "PEN"))
                .build();

        ESDocumentUserQueryParser parser = new ESDocumentUserQueryParser();
        String esQuery = parser.getQuery(user, query, new MockSpaceModel("46779354"));

        Mockito.verify(user, Mockito.atLeastOnce()).getAllPermitedSpaces();
        assertThat(esQuery).isNotNull();

        assertThat(esQuery).isEqualTo("{\n" +
                "  \"bool\" : {\n" +
                "    \"filter\" : [\n" +
                "      {\n" +
                "        \"term\" : {\n" +
                "          \"currency\" : {\n" +
                "            \"value\" : \"PEN\",\n" +
                "            \"boost\" : 1.0\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"should\" : [\n" +
                "      {\n" +
                "        \"terms\" : {\n" +
                "          \"supplierAssignedId\" : [\n" +
                "            \"46779354\"\n" +
                "          ],\n" +
                "          \"boost\" : 1.0\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"terms\" : {\n" +
                "          \"customerAssignedId\" : [\n" +
                "            \"46779354\"\n" +
                "          ],\n" +
                "          \"boost\" : 1.0\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"disable_coord\" : false,\n" +
                "    \"adjust_pure_negative\" : true,\n" +
                "    \"minimum_should_match\" : \"1\",\n" +
                "    \"boost\" : 1.0\n" +
                "  }\n" +
                "}");
    }

    @Test
    public void getQueryOnDocumentUser() throws Exception {
        HashSet<SpaceModel> permittedSpaces = new HashSet<>();
        permittedSpaces.add(new MockSpaceModel("46779354"));
        permittedSpaces.add(new MockSpaceModel("10467793549"));
        permittedSpaces.add(new MockSpaceModel("123456789"));

        Mockito.when(user.getAllPermitedSpaces()).thenReturn(permittedSpaces);

        DocumentUserQueryModel query = DocumentUserQueryModel.builder()
                .addUserDocumentFilter(new TermQuery(DocumentUserModel.VIEWED, true))
                .build();

        ESDocumentUserQueryParser parser = new ESDocumentUserQueryParser();
        String esQuery = parser.getQuery(user, query, new MockSpaceModel("46779354"));

        Mockito.verify(user, Mockito.atLeastOnce()).getAllPermitedSpaces();
        assertThat(esQuery).isNotNull();

        assertThat(esQuery).isEqualTo("{\n" +
                "  \"bool\" : {\n" +
                "    \"filter\" : [\n" +
                "      {\n" +
                "        \"term\" : {\n" +
                "          \"viewed\" : {\n" +
                "            \"value\" : true,\n" +
                "            \"boost\" : 1.0\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"should\" : [\n" +
                "      {\n" +
                "        \"terms\" : {\n" +
                "          \"supplierAssignedId\" : [\n" +
                "            \"46779354\"\n" +
                "          ],\n" +
                "          \"boost\" : 1.0\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"terms\" : {\n" +
                "          \"customerAssignedId\" : [\n" +
                "            \"46779354\"\n" +
                "          ],\n" +
                "          \"boost\" : 1.0\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"disable_coord\" : false,\n" +
                "    \"adjust_pure_negative\" : true,\n" +
                "    \"minimum_should_match\" : \"1\",\n" +
                "    \"boost\" : 1.0\n" +
                "  }\n" +
                "}");
    }

    @Test
    public void getQueryOnDocumentAndUser() throws Exception {
        HashSet<SpaceModel> permittedSpaces = new HashSet<>();
        permittedSpaces.add(new MockSpaceModel("46779354"));
        permittedSpaces.add(new MockSpaceModel("10467793549"));
        permittedSpaces.add(new MockSpaceModel("123456789"));

        Mockito.when(user.getAllPermitedSpaces()).thenReturn(permittedSpaces);

        DocumentUserQueryModel query = DocumentUserQueryModel.builder()
                .addDocumentFilter(new TermQuery(DocumentModel.TYPE, "Invoice"))
                .addUserDocumentFilter(new TermQuery(DocumentUserModel.VIEWED, true))
                .build();

        ESDocumentUserQueryParser parser = new ESDocumentUserQueryParser();
        String esQuery = parser.getQuery(user, query, new MockSpaceModel("46779354"));

        Mockito.verify(user, Mockito.atLeastOnce()).getAllPermitedSpaces();
        assertThat(esQuery).isNotNull();

        assertThat(esQuery).isEqualTo("{\n" +
                "  \"bool\" : {\n" +
                "    \"filter\" : [\n" +
                "      {\n" +
                "        \"term\" : {\n" +
                "          \"document.type\" : {\n" +
                "            \"value\" : \"Invoice\",\n" +
                "            \"boost\" : 1.0\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"term\" : {\n" +
                "          \"viewed\" : {\n" +
                "            \"value\" : true,\n" +
                "            \"boost\" : 1.0\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"should\" : [\n" +
                "      {\n" +
                "        \"terms\" : {\n" +
                "          \"supplierAssignedId\" : [\n" +
                "            \"46779354\"\n" +
                "          ],\n" +
                "          \"boost\" : 1.0\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"terms\" : {\n" +
                "          \"customerAssignedId\" : [\n" +
                "            \"46779354\"\n" +
                "          ],\n" +
                "          \"boost\" : 1.0\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"disable_coord\" : false,\n" +
                "    \"adjust_pure_negative\" : true,\n" +
                "    \"minimum_should_match\" : \"1\",\n" +
                "    \"boost\" : 1.0\n" +
                "  }\n" +
                "}");
    }

    public static class MockSpaceModel extends SpaceAdapter {

        private String assignedId;

        public MockSpaceModel(String assignedId) {
            super(null, null);
            this.assignedId = assignedId;
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public String getAssignedId() {
            return assignedId;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void setName(String name) {

        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public void setDescription(String description) {

        }

        @Override
        public UserModel getOwner() {
            return null;
        }

        @Override
        public void setOwner(UserModel user) {

        }

        @Override
        public Date getCreatedAt() {
            return null;
        }

        @Override
        public Date getUpdatedAt() {
            return null;
        }

        @Override
        public List<UserModel> getCollaborators() {
            return null;
        }

        @Override
        public List<UserModel> getCollaborators(int offset, int limit) {
            return null;
        }

        @Override
        public void addCollaborators(UserModel user) {

        }

        @Override
        public boolean removeCollaborators(UserModel user) {
            return false;
        }
    }

}