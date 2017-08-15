package org.openfact.models;

import org.arquillian.ape.rdbms.Cleanup;
import org.arquillian.ape.rdbms.CleanupStrategy;
import org.arquillian.ape.rdbms.TestExecutionPhase;
import org.junit.Test;
import org.openfact.models.utils.OpenfactModelUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentProviderTest extends AbstractModelTest {

    @Inject
    public DocumentProvider documentProvider;

    @Inject
    public FileProvider fileProvider;

    @Test
    @Cleanup(phase = TestExecutionPhase.BEFORE, strategy = CleanupStrategy.STRICT)
    public void createDocumentTest() throws StorageException, IOException, SAXException, ParserConfigurationException, TransformerException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("documents/BB11-1.xml");
        assertThat(is).isNotNull();
        Document xml = OpenfactModelUtils.toDocument(is);
        byte[] bytes = OpenfactModelUtils.toByteArray(xml);

        FileModel file = fileProvider.addFile(bytes, ".xml");
        DocumentModel document1 = documentProvider.addDocument(file);

        assertThat(document1).isNotNull()
                .matches(u -> u.getId() != null)
                .matches(u -> u.getFileId().equals(file.getId()))
                .matches(u -> u.getType().equals("Invoice"))
                .matches(u -> u.getAssignedId().equals("BB11-1"));
    }

}
