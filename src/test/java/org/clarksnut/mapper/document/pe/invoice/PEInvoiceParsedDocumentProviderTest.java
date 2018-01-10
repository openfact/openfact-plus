package org.clarksnut.mapper.document.pe.invoice;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PEInvoiceParsedDocumentProviderTest {

    @Mock
    private XmlUBLFileModel file;

    @Test
    public void readFF11_00000003() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/invoice/FF11-00000003.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PEInvoiceParsedDocumentProvider provider = new PEInvoiceParsedDocumentProvider();
        DocumentMapped parsedDocument = provider.map(file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();


        assertThat(parsedDocument).isNotNull();
        assertThat(parsedDocument.getType() instanceof InvoiceType).isEqualTo(true);
        assertThat(parsedDocument.getType()).isNotNull();


        DocumentBean skeleton = parsedDocument.getBean();

        assertThat(skeleton.getAssignedId()).isEqualTo("FF11-00000003");
        assertThat(skeleton.getAmount()).isEqualTo(138.65F);
        assertThat(skeleton.getTax()).isEqualTo(21.15F);
        assertThat(skeleton.getCurrency()).isEqualTo("PEN");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 46);
        calendar.set(Calendar.SECOND, 20);
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