package org.clarksnut.mapper.document.pe.perception;

import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openfact.perception.PerceptionType;

import java.io.InputStream;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PEPerceptionParsedDocumentProviderTest {

    @Mock
    private XmlUBLFileModel file;

    @Test
    public void readP001_123() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/perception/P001-123.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PEPerceptionParsedDocumentProvider provider = new PEPerceptionParsedDocumentProvider();
        DocumentMapped parsedDocument = provider.map(file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();


        assertThat(parsedDocument).isNotNull();
        assertThat(parsedDocument.getType() instanceof PerceptionType).isEqualTo(true);
        assertThat(parsedDocument.getType()).isNotNull();


        DocumentBean skeleton = parsedDocument.getBean();

        assertThat(skeleton.getAssignedId()).isEqualTo("P001-123");
        assertThat(skeleton.getAmount()).isEqualTo(17_544F);
        assertThat(skeleton.getTax()).isNull();
        assertThat(skeleton.getCurrency()).isEqualTo("PEN");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2015);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 23);
        assertThat(skeleton.getIssueDate()).isEqualTo(calendar.getTime());

        assertThat(skeleton.getSupplierName()).isEqualTo("K&G Asociados S. A.");
        assertThat(skeleton.getSupplierAssignedId()).isEqualTo("20100113612");
        assertThat(skeleton.getSupplierStreetAddress()).isEqualTo("AV. LOS OLIVOS 767");
        assertThat(skeleton.getSupplierCity()).isEqualTo("MOLINA, LIMA, LIMA");
        assertThat(skeleton.getSupplierCountry()).isEqualTo("PE");

        assertThat(skeleton.getCustomerName()).isEqualTo("CIA. DE CONSULTORIA Y PLANEAMIENTO S.A.C.");
        assertThat(skeleton.getCustomerAssignedId()).isEqualTo("20546772439");
        assertThat(skeleton.getCustomerStreetAddress()).isEqualTo("CAL. CALLE MORELLI 181 INT. P-2");
        assertThat(skeleton.getCustomerCity()).isEqualTo("SAN BORJA, LIMA, LIMA");
        assertThat(skeleton.getCustomerCountry()).isEqualTo("PE");
    }

}