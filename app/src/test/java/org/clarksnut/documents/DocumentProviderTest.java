package org.clarksnut.documents;

import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.junit.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentProviderTest extends AbstractProviderTest {

    @Inject
    private DocumentProvider documentProvider;

    @Test
    public void addAndGetDocument() throws Exception {
        XmlUBLFileModel file = Mockito.mock(XmlUBLFileModel.class);

        InputStream is = getClass().getClassLoader().getResourceAsStream("/peru/document/invoice/FF11-00000003.xml");

        assertThat(is).isNotNull();

        String fileId = UUID.randomUUID().toString();

        Mockito.when(file.getDocumentType()).thenReturn("Invoice");
        Mockito.when(file.getId()).thenReturn(fileId);
        Mockito.when(file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        DocumentModel document = documentProvider.addDocument(file, "jpa", true, DocumentProviderType.USER);

        Mockito.verify(file, Mockito.atLeastOnce()).getDocumentType();
        Mockito.verify(file, Mockito.atLeastOnce()).getId();
        Mockito.verify(file, Mockito.atLeastOnce()).getDocument();

        assertThat(document).as("check document").isNotNull();
        assertThat(document.getId()).as("check document id").isNotNull();
        assertThat(document.getType()).as("check document type").isEqualTo("Invoice");
        assertThat(document.getFileId()).as("check fileId is %s", fileId).isEqualTo(fileId);
        assertThat(document.isVerified()).as("check isVerified is %s", true).isEqualTo(true);
        assertThat(document.getProvider()).as("check provider is %s", DocumentProviderType.USER).isEqualTo(DocumentProviderType.USER);

        // Second version
        document = documentProvider.addDocument(file, "jpa", true, DocumentProviderType.USER);

        assertThat(document).as("check second version document").isNotNull();


        // get document by id
        document = documentProvider.getDocument(document.getId());
        assertThat(document).as("check document by id").isNotNull();

        // get document by attributes
        document = documentProvider.getDocument("Invoice", "FF11-00000003", "20494637074");
        assertThat(document).as("check document by %s, %s, %s", "Invoice", "FF11-00000003", "20494637074").isNotNull();

        // get not existed
        DocumentModel unexistedDocument = documentProvider.getDocument("1");
        assertThat(unexistedDocument).as("check not existed document").isNull();

        // Remove document
        String documentId = document.getId();

        boolean result = documentProvider.removeDocument(document);
        document = documentProvider.getDocument(documentId);

        assertThat(result).as("check remove document").isEqualTo(true);
        assertThat(document).as("check document was removed").isNull();
    }

}