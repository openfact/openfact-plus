package org.clarksnut.documents;

import org.clarksnut.documents.exceptions.UnreadableDocumentException;
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
    public void before() throws UnsupportedDocumentTypeException, UnreadableDocumentException, IOException, SAXException, ParserConfigurationException {
        Map<String, String> xmls = new HashMap<>();
        xmls.put("Invoice", "/peru/document/invoice/FF11-00000003.xml");
        xmls.put("Invoice", "/peru/document/invoice/BB11-1.xml");
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

            DocumentModel document = documentProvider.addDocument(file, true, DocumentProviderType.USER);

            documentIds.add(document.getId());
        }
    }

    @Test
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

    @Test
    public void getDocumentsUser() throws Exception {
    }

    @Test
    public void getDocumentsUserSize() throws Exception {
    }

}