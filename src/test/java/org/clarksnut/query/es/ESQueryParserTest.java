package org.clarksnut.query.es;

import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(MockitoJUnitRunner.class)
public class ESQueryParserTest {

//    @Test
//    public void toQueryBuilderWithNullQueries() throws Exception {
//        IndexedDocumentQueryModel query = IndexedDocumentQueryModel.builder().build();
//
//        boolean exception = false;
//        try {
//            ESQueryParser.toQueryBuilder(null, new DocumentFieldMapper());
//        } catch (IllegalStateException e) {
//            exception = true;
//        }
//
//        assertThat(exception).isEqualTo(true);
//    }
//
//    @Test
//    public void toQueryBuilderWithNotAllowedSpacesByUser() throws Exception {
//        Mockito.when(user.getAllPermitedSpaces()).thenReturn(new HashSet<>());
//
//        IndexedDocumentQueryModel query = IndexedDocumentQueryModel.builder()
//                .addFilter(new TermQuery(DocumentModel.CURRENCY, "PEN"))
//                .build();
//
//        String esQuery = ESQueryParser.toQueryBuilder(user, query);
//
//        Mockito.verify(user, Mockito.atLeastOnce()).getAllPermitedSpaces();
//        assertThat(esQuery).isNull();
//    }
//
//    @Test
//    public void toQueryBuilderWithNoIntersection() throws Exception {
//        HashSet<SpaceModel> permittedSpaces = new HashSet<>();
//        permittedSpaces.add(new MockSpaceModel("46779354"));
//        permittedSpaces.add(new MockSpaceModel("10467793549"));
//        permittedSpaces.add(new MockSpaceModel("123456789"));
//
//        Mockito.when(user.getAllPermitedSpaces()).thenReturn(permittedSpaces);
//
//        IndexedDocumentQueryModel query = IndexedDocumentQueryModel.builder()
//                .addFilter(new TermQuery(DocumentModel.CURRENCY, "PEN"))
//                .build();
//
//        ESQueryParser parser = new ESQueryParser();
//        String esQuery = ESQueryParser.toQueryBuilder(user, query, new MockSpaceModel("00000000"));
//
//        Mockito.verify(user, Mockito.atLeastOnce()).getAllPermitedSpaces();
//        assertThat(esQuery).isNull();
//    }
//
//    @Test
//    public void toQueryBuilderOnDocument() throws Exception {
//        HashSet<SpaceModel> permittedSpaces = new HashSet<>();
//        permittedSpaces.add(new MockSpaceModel("46779354"));
//        permittedSpaces.add(new MockSpaceModel("10467793549"));
//        permittedSpaces.add(new MockSpaceModel("123456789"));
//
//        Mockito.when(user.getAllPermitedSpaces()).thenReturn(permittedSpaces);
//
//        IndexedDocumentQueryModel query = IndexedDocumentQueryModel.builder()
//                .addFilter(new TermQuery(DocumentModel.CURRENCY, "PEN"))
//                .build();
//
//        ESQueryParser parser = new ESQueryParser();
//        String esQuery = ESQueryParser.toQueryBuilder(user, query, new MockSpaceModel("46779354"));
//
//        Mockito.verify(user, Mockito.atLeastOnce()).getAllPermitedSpaces();
//        assertThat(esQuery).isNotNull();
//
//        assertThat(esQuery).isEqualTo("{\n" +
//                "  \"bool\" : {\n" +
//                "    \"filter\" : [\n" +
//                "      {\n" +
//                "        \"term\" : {\n" +
//                "          \"currency\" : {\n" +
//                "            \"value\" : \"PEN\",\n" +
//                "            \"boost\" : 1.0\n" +
//                "          }\n" +
//                "        }\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    \"should\" : [\n" +
//                "      {\n" +
//                "        \"terms\" : {\n" +
//                "          \"supplierAssignedId\" : [\n" +
//                "            \"46779354\"\n" +
//                "          ],\n" +
//                "          \"boost\" : 1.0\n" +
//                "        }\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"terms\" : {\n" +
//                "          \"customerAssignedId\" : [\n" +
//                "            \"46779354\"\n" +
//                "          ],\n" +
//                "          \"boost\" : 1.0\n" +
//                "        }\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    \"disable_coord\" : false,\n" +
//                "    \"adjust_pure_negative\" : true,\n" +
//                "    \"minimum_should_match\" : \"1\",\n" +
//                "    \"boost\" : 1.0\n" +
//                "  }\n" +
//                "}");
//    }
//
//    @Test
//    public void toQueryBuilderOnDocumentUser() throws Exception {
//        HashSet<SpaceModel> permittedSpaces = new HashSet<>();
//        permittedSpaces.add(new MockSpaceModel("46779354"));
//        permittedSpaces.add(new MockSpaceModel("10467793549"));
//        permittedSpaces.add(new MockSpaceModel("123456789"));
//
//        Mockito.when(user.getAllPermitedSpaces()).thenReturn(permittedSpaces);
//
//        IndexedDocumentQueryModel query = IndexedDocumentQueryModel.builder()
//                .addUserDocumentFilter(new TermQuery(DocumentUserModel.VIEWED, true))
//                .build();
//
//        ESQueryParser parser = new ESQueryParser();
//        String esQuery = ESQueryParser.toQueryBuilder(user, query, new MockSpaceModel("46779354"));
//
//        Mockito.verify(user, Mockito.atLeastOnce()).getAllPermitedSpaces();
//        assertThat(esQuery).isNotNull();
//
//        assertThat(esQuery).isEqualTo("{\n" +
//                "  \"bool\" : {\n" +
//                "    \"filter\" : [\n" +
//                "      {\n" +
//                "        \"term\" : {\n" +
//                "          \"viewed\" : {\n" +
//                "            \"value\" : true,\n" +
//                "            \"boost\" : 1.0\n" +
//                "          }\n" +
//                "        }\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    \"should\" : [\n" +
//                "      {\n" +
//                "        \"terms\" : {\n" +
//                "          \"supplierAssignedId\" : [\n" +
//                "            \"46779354\"\n" +
//                "          ],\n" +
//                "          \"boost\" : 1.0\n" +
//                "        }\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"terms\" : {\n" +
//                "          \"customerAssignedId\" : [\n" +
//                "            \"46779354\"\n" +
//                "          ],\n" +
//                "          \"boost\" : 1.0\n" +
//                "        }\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    \"disable_coord\" : false,\n" +
//                "    \"adjust_pure_negative\" : true,\n" +
//                "    \"minimum_should_match\" : \"1\",\n" +
//                "    \"boost\" : 1.0\n" +
//                "  }\n" +
//                "}");
//    }
//
//    @Test
//    public void toQueryBuilderOnDocumentAndUser() throws Exception {
//        HashSet<SpaceModel> permittedSpaces = new HashSet<>();
//        permittedSpaces.add(new MockSpaceModel("46779354"));
//        permittedSpaces.add(new MockSpaceModel("10467793549"));
//        permittedSpaces.add(new MockSpaceModel("123456789"));
//
//        Mockito.when(user.getAllPermitedSpaces()).thenReturn(permittedSpaces);
//
//        IndexedDocumentQueryModel query = IndexedDocumentQueryModel.builder()
//                .addFilter(new TermQuery(DocumentModel.TYPE, "Invoice"))
//                .addUserDocumentFilter(new TermQuery(DocumentUserModel.VIEWED, true))
//                .build();
//
//        ESQueryParser parser = new ESQueryParser();
//        String esQuery = ESQueryParser.toQueryBuilder(user, query, new MockSpaceModel("46779354"));
//
//        Mockito.verify(user, Mockito.atLeastOnce()).getAllPermitedSpaces();
//        assertThat(esQuery).isNotNull();
//
//        assertThat(esQuery).isEqualTo("{\n" +
//                "  \"bool\" : {\n" +
//                "    \"filter\" : [\n" +
//                "      {\n" +
//                "        \"term\" : {\n" +
//                "          \"document.type\" : {\n" +
//                "            \"value\" : \"Invoice\",\n" +
//                "            \"boost\" : 1.0\n" +
//                "          }\n" +
//                "        }\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"term\" : {\n" +
//                "          \"viewed\" : {\n" +
//                "            \"value\" : true,\n" +
//                "            \"boost\" : 1.0\n" +
//                "          }\n" +
//                "        }\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    \"should\" : [\n" +
//                "      {\n" +
//                "        \"terms\" : {\n" +
//                "          \"supplierAssignedId\" : [\n" +
//                "            \"46779354\"\n" +
//                "          ],\n" +
//                "          \"boost\" : 1.0\n" +
//                "        }\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"terms\" : {\n" +
//                "          \"customerAssignedId\" : [\n" +
//                "            \"46779354\"\n" +
//                "          ],\n" +
//                "          \"boost\" : 1.0\n" +
//                "        }\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    \"disable_coord\" : false,\n" +
//                "    \"adjust_pure_negative\" : true,\n" +
//                "    \"minimum_should_match\" : \"1\",\n" +
//                "    \"boost\" : 1.0\n" +
//                "  }\n" +
//                "}");
//    }

}