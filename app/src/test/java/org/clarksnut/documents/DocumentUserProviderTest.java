package org.clarksnut.documents;

import org.arquillian.ape.rdbms.Cleanup;
import org.arquillian.ape.rdbms.CleanupStrategy;
import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.clarksnut.documents.exceptions.UnreadableDocumentException;
import org.clarksnut.documents.exceptions.UnrecognizableDocumentTypeException;
import org.clarksnut.documents.exceptions.UnsupportedDocumentTypeException;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.SpaceModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.transaction.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentUserProviderTest extends AbstractProviderTest {

    @Inject
    private DocumentProvider documentProvider;

    @Inject
    private DocumentUserProvider documentUserProvider;

    private List<String> documentIds;

    @Before
    public void before() throws UnsupportedDocumentTypeException, UnreadableDocumentException, IOException, SAXException, ParserConfigurationException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException, UnrecognizableDocumentTypeException {
        Map<String, String> xmls = new HashMap<>();
        xmls.put("Invoice", "/peru/document/invoice/FF11-00000003.xml");
        xmls.put("CreditNote", "/peru/document/creditnote/FF11-3.xml");
        xmls.put("DebitNote", "/peru/document/debitnote/FF11-5.xml");

        documentIds = new ArrayList<>();

        for (Map.Entry<String, String> entry : xmls.entrySet()) {
            XmlUBLFileModel file = Mockito.mock(XmlUBLFileModel.class);
            InputStream is = getClass().getClassLoader().getResourceAsStream(entry.getValue());
            assertThat(is).as("check document before creation").isNotNull();

            Mockito.when(file.getDocumentType()).thenReturn(entry.getKey());
            Mockito.when(file.getId()).thenReturn(UUID.randomUUID().toString());
            Mockito.when(file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

            DocumentModel document = documentProvider.addDocument(file, "jpa",true, DocumentProviderType.USER);

            documentIds.add(document.getId());
        }
    }

    @Test
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void getDocumentUser() throws Exception {
        SpaceModel space = Mockito.mock(SpaceModel.class);
        Mockito.when(space.getAssignedId()).thenReturn("20494637074");

        UserModel user = Mockito.mock(UserModel.class);
        Mockito.when(user.getId()).thenReturn("1");
        Mockito.when(user.getAllPermitedSpaces()).thenReturn(new HashSet<>(Arrays.asList(space)));

        String documentId = documentIds.get(0);
        DocumentUserModel documentUser = documentUserProvider.getDocumentUser(user, documentId);

        Mockito.verify(space, Mockito.atLeastOnce()).getAssignedId();
        Mockito.verify(user, Mockito.atLeastOnce()).getId();
        Mockito.verify(user, Mockito.atLeastOnce()).getAllPermitedSpaces();

        assertThat(documentUser).as("check first documentUser").isNotNull();

        // Get again to force search
        documentUser = documentUserProvider.getDocumentUser(user, documentId);
        assertThat(documentUser).as("check second documentUser").isNotNull();

        // Check when user is no longer permitted to see a certain space
        Mockito.when(user.getAllPermitedSpaces()).thenReturn(new HashSet<>());

        documentUser = documentUserProvider.getDocumentUser(user, documentId);
        assertThat(documentUser).as("check third documentUser").isNull();
    }

//    @Test
//    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
//    public void getDocumentsUser() throws Exception {
//        SpaceModel space = Mockito.mock(SpaceModel.class);
//        Mockito.when(space.getAssignedId()).thenReturn("20494637074");
//
//        UserModel user = Mockito.mock(UserModel.class);
//        Mockito.when(user.getAllPermitedSpaces()).thenReturn(new HashSet<>(Arrays.asList(space)));
//
//        // Get all
//        DocumentUserQueryModel query = DocumentUserQueryModel.builder()
//                .addDocumentFilter(new MatchAllQuery())
//                .build();
//        SearchResultModel<DocumentUserModel> documentsUser = documentUserProvider.getDocumentsUser(user, query);
//
//        Mockito.verify(space, Mockito.atLeastOnce()).getAssignedId();
//        Mockito.verify(user, Mockito.atLeastOnce()).getAllPermitedSpaces();
//
//        assertThat(documentsUser).as("check search all").isNotNull();
//        assertThat(documentsUser.getItems().size()).as("check search all size").isEqualTo(3);
//
//        // Get
//        query = DocumentUserQueryModel.builder()
//                .addDocumentFilter(new TermQuery(DocumentModel.TYPE, "Invoice"))
//                .build();
//        documentsUser = documentUserProvider.getDocumentsUser(user, query);
//
//        assertThat(documentsUser).as("check search by type").isNotNull();
//        assertThat(documentsUser.getItems().size()).as("check search by type results").isEqualTo(1);
//    }
//
//    @Test
//    public void getDocumentsUserSize() throws Exception {
//    }

}