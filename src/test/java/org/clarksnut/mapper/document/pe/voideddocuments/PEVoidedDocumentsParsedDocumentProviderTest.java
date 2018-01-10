package org.clarksnut.mapper.document.pe.voideddocuments;

import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;

import java.io.InputStream;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PEVoidedDocumentsParsedDocumentProviderTest {

    @Mock
    private XmlUBLFileModel file;

    @Test
    public void readRA_20170223_00004() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/voideddocuments/RA-20170223-00004.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PEVoidedDocumentsParsedDocumentProvider provider = new PEVoidedDocumentsParsedDocumentProvider();
        DocumentMapped parsedDocument = provider.map(file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();


        assertThat(parsedDocument).isNotNull();
        assertThat(parsedDocument.getType() instanceof VoidedDocumentsType).isEqualTo(true);
        assertThat(parsedDocument.getType()).isNotNull();


        DocumentBean skeleton = parsedDocument.getBean();

        assertThat(skeleton.getAssignedId()).isEqualTo("RA-20170223-00004");
        assertThat(skeleton.getAmount()).isNull();
        assertThat(skeleton.getTax()).isNull();
        assertThat(skeleton.getCurrency()).isNull();

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 23);
        assertThat(skeleton.getIssueDate()).isEqualTo(calendar.getTime());

        assertThat(skeleton.getSupplierName()).isEqualTo("AHREN CONTRATISTAS GENERALES S.A.C");
        assertThat(skeleton.getSupplierAssignedId()).isEqualTo("20494637074");
        assertThat(skeleton.getSupplierStreetAddress()).isEqualTo("Mza. A Lote. 3 A.v. Santa Teresa");
        assertThat(skeleton.getSupplierCity()).isEqualTo("Huamanga, Ayacucho, Ayacucho");
        assertThat(skeleton.getSupplierCountry()).isEqualTo("PE");

        assertThat(skeleton.getCustomerName()).isNull();
        assertThat(skeleton.getCustomerAssignedId()).isNull();
        assertThat(skeleton.getCustomerStreetAddress()).isNull();
        assertThat(skeleton.getCustomerCity()).isNull();
        assertThat(skeleton.getCustomerCountry()).isNull();
    }

}