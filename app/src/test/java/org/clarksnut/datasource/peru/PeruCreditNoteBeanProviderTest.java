package org.clarksnut.datasource.peru;

import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PeruCreditNoteBeanProviderTest {

    @Mock
    private DocumentModel document;

    @Mock
    private XmlFileModel file;

    @Test
    public void getDatasource() throws Exception {
        InputStream is = getClass().getResourceAsStream("/peru/document/creditnote/FF11-3.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruCreditNoteBeanProvider datasourceProvider = new PeruCreditNoteBeanProvider();
        CreditNoteDatasource creditNote = (CreditNoteDatasource) datasourceProvider.getDatasource(this.document, this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

//        assertThat(creditNote).isNotNull();
//        assertThat(creditNote.getTipoDocumento()).isEqualTo("BOLETA");
//        assertThat(creditNote.getIdAsignado()).isEqualTo("BB11-1");
//        assertThat(creditNote.getMoneda()).isEqualTo("PEN");
//
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.clear();
//        calendar.set(Calendar.YEAR, 2017);
//        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
//        calendar.set(Calendar.DAY_OF_MONTH, 22);
//        calendar.set(Calendar.HOUR_OF_DAY, 20);
//        calendar.set(Calendar.MINUTE, 46);
//        calendar.set(Calendar.SECOND, 47);
//        assertThat(creditNote.getFechaEmision()).isEqualTo(calendar.getTime());
//        assertThat(creditNote.getFechaVencimiento()).isNull();
//
//        assertThat(creditNote.getTotalIgv()).isEqualTo(270F);
//        assertThat(creditNote.getTotalIsc()).isNull();
//        assertThat(creditNote.getTotalOtrosTributos()).isNull();
//
//        assertThat(creditNote.getTotalGravada()).isEqualTo(1_500F);
//        assertThat(creditNote.getTotalInafecta()).isNull();
//        assertThat(creditNote.getTotalExonerada()).isNull();
//        assertThat(creditNote.getTotalGratuita()).isNull();
//
//        assertSupplier(creditNote.getProveedor(),
//                "20494637074",
//                "RUC",
//                "AHREN CONTRATISTAS GENERALES S.A.C");
//        assertPostalAddress(creditNote.getProveedor().getDireccion(),
//                "050101",
//                "Mza. A Lote. 3 A.v. Santa Teresa",
//                "Huamanga",
//                "Ayacucho",
//                "Ayacucho",
//                "PE");
//        assertCustomer(creditNote.getCliente(),
//                "46779354",
//                "DNI",
//                "CARLOS ESTEBAN FERIA VILA");
//        assertThat(creditNote.getCliente().getDireccion()).isNull();
//
//        // Lines
//        assertThat(creditNote.getDetalle().size()).isEqualTo(4);
//
//        LineBean line = creditNote.getDetalle().get(3);
//
//        assertLine(line,
//                4F,
//                "NIU",
//                null,
//                "MALETIN",
//                200F,
//                0F,
//                236F,
//                800F,
//                null,
//                144F,
//                null
//        );
    }

}