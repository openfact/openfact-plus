package org.clarksnut.mapper.document.pe.debitnote;

import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.clarksnut.mapper.document.DocumentMapped.DocumentBean;

@RunWith(MockitoJUnitRunner.class)
public class PEDebitNoteParsedDocumentProviderTest {

    @Mock
    private XmlUBLFileModel file;

    @Test
    public void readFF11_5() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/debitnote/FF11-5.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PEDebitNoteParsedDocumentProvider provider = new PEDebitNoteParsedDocumentProvider();
        DocumentMapped parsedDocument = provider.map(file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();


        assertThat(parsedDocument).isNotNull();
        assertThat(parsedDocument.getType() instanceof DebitNoteType).isEqualTo(true);
        assertThat(parsedDocument.getType()).isNotNull();


        DocumentBean skeleton = parsedDocument.getBean();

        assertThat(skeleton.getAssignedId()).isEqualTo("FF11-5");
        assertThat(skeleton.getAmount()).isEqualTo(375.24F);
        assertThat(skeleton.getTax()).isEqualTo(57.24F);
        assertThat(skeleton.getCurrency()).isEqualTo("PEN");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 31);
        calendar.set(Calendar.SECOND, 27);
        assertThat(skeleton.getIssueDate()).isEqualTo(calendar.getTime());

        assertThat(skeleton.getSupplierName()).isEqualTo("AHREN CONTRATISTAS GENERALES S.A.C");
        assertThat(skeleton.getSupplierAssignedId()).isEqualTo("20494637074");
        assertThat(skeleton.getSupplierStreetAddress()).isEqualTo("Mza. A Lote. 3 A.v. Santa Teresa");
        assertThat(skeleton.getSupplierCity()).isEqualTo("Huamanga, Ayacucho, Ayacucho");
        assertThat(skeleton.getSupplierCountry()).isEqualTo("PE");

        assertThat(skeleton.getCustomerName()).isEqualTo("CARLOS ESTEBAN FERIA VILA");
        assertThat(skeleton.getCustomerAssignedId()).isEqualTo("10467793549");
        assertThat(skeleton.getCustomerStreetAddress()).isNull();
        assertThat(skeleton.getCustomerCity()).isNull();
        assertThat(skeleton.getCustomerCountry()).isNull();
    }

}