package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.peru.beans.LineBean;
import org.clarksnut.files.XmlUBLFileModel;
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
public class PeruCreditNoteBeanProviderTest extends PeruBeanUtilsTest {

    @Mock
    private XmlUBLFileModel file;

    @Test
    public void getDatasourceFF11_3() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/creditnote/FF11-3.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruCreditNoteDatasourceProvider datasourceProvider = new PeruCreditNoteDatasourceProvider();
        PeruCreditNoteDatasource creditNote = (PeruCreditNoteDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(creditNote).isNotNull();
        assertThat(creditNote.getIdAsignado()).isEqualTo("FF11-3");
        assertThat(creditNote.getTipoNotaCredito()).isEqualTo("ANULACION DE LA OPERACION");
        assertThat(creditNote.getMoneda()).isEqualTo("PEN");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 48);
        calendar.set(Calendar.SECOND, 34);
        assertThat(creditNote.getFechaEmision()).isEqualTo(calendar.getTime());

        assertThat(creditNote.getDocumentoModifica()).isEqualTo("FF11-00000004");
        assertThat(creditNote.getTipoDocumentoModifica()).isEqualTo("FACTURA");
        assertThat(creditNote.getMotivoSustento()).isEqualTo("SIN OBSERVACIONES");

        assertThat(creditNote.getTributos().getTotalIgv()).isEqualTo(57.24F);
        assertThat(creditNote.getTributos().getTotalIsc()).isNull();
        assertThat(creditNote.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(creditNote.getInformacionAdicional().getTotalGravada()).isEqualTo(318F);
        assertThat(creditNote.getInformacionAdicional().getTotalInafecta()).isNull();
        assertThat(creditNote.getInformacionAdicional().getTotalExonerada()).isNull();
        assertThat(creditNote.getInformacionAdicional().getTotalGratuita()).isNull();

        assertThat(creditNote.getTotalVenta()).isEqualTo(375.24F);
        assertThat(creditNote.getTotalOtrosCargos()).isNull();
        assertThat(creditNote.getTotalDescuentoGlobal()).isEqualTo(0F);

        assertSupplier(creditNote.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(creditNote.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(creditNote.getCliente(),
                "10467793549",
                "RUC",
                "CARLOS ESTEBAN FERIA VILA");
        assertThat(creditNote.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(creditNote.getDetalle().size()).isEqualTo(2);

        LineBean line = creditNote.getDetalle().get(1);

        assertLine(line,
                1F,
                "NIU",
                null,
                "MALETIN",
                236F,
                236F,
                null,
                200F,
                null,
                36F,
                null
        );
    }

    @Test
    public void getDatasourceFF12_1() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/creditnote/FF12-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruCreditNoteDatasourceProvider datasourceProvider = new PeruCreditNoteDatasourceProvider();
        PeruCreditNoteDatasource creditNote = (PeruCreditNoteDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(creditNote).isNotNull();
        assertThat(creditNote.getIdAsignado()).isEqualTo("FF12-1");
        assertThat(creditNote.getTipoNotaCredito()).isEqualTo("ANULACION DE LA OPERACION");
        assertThat(creditNote.getMoneda()).isEqualTo("PEN");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 13);
        calendar.set(Calendar.SECOND, 44);
        assertThat(creditNote.getFechaEmision()).isEqualTo(calendar.getTime());

        assertThat(creditNote.getDocumentoModifica()).isEqualTo("FF12-3");
        assertThat(creditNote.getTipoDocumentoModifica()).isEqualTo("FACTURA");
        assertThat(creditNote.getMotivoSustento()).isEqualTo("SIN OBSERVACIONES");

        assertThat(creditNote.getTributos().getTotalIgv()).isNull();
        assertThat(creditNote.getTributos().getTotalIsc()).isNull();
        assertThat(creditNote.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(creditNote.getInformacionAdicional().getTotalGravada()).isNull();
        assertThat(creditNote.getInformacionAdicional().getTotalInafecta()).isNull();
        assertThat(creditNote.getInformacionAdicional().getTotalExonerada()).isEqualTo(1_800F);
        assertThat(creditNote.getInformacionAdicional().getTotalGratuita()).isNull();

        assertThat(creditNote.getTotalVenta()).isEqualTo(1_800F);
        assertThat(creditNote.getTotalOtrosCargos()).isNull();
        assertThat(creditNote.getTotalDescuentoGlobal()).isNull();

        assertSupplier(creditNote.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(creditNote.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(creditNote.getCliente(),
                "10467793549",
                "RUC",
                "CARLOS ESTEBAN FERIA VILA");
        assertThat(creditNote.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(creditNote.getDetalle().size()).isEqualTo(1);

        LineBean line = creditNote.getDetalle().get(0);

        assertLine(line,
                1F,
                "NIU",
                null,
                "CELULAR",
                1_800F,
                1_800F,
                null,
                1_800F,
                null,
                0F,
                null
        );
    }

    @Test
    public void getDatasourceFF14_1() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/creditnote/FF14-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruCreditNoteDatasourceProvider datasourceProvider = new PeruCreditNoteDatasourceProvider();
        PeruCreditNoteDatasource creditNote = (PeruCreditNoteDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(creditNote).isNotNull();
        assertThat(creditNote.getIdAsignado()).isEqualTo("FF14-1");
        assertThat(creditNote.getTipoNotaCredito()).isEqualTo("DEVOLUCION TOTAL");
        assertThat(creditNote.getMoneda()).isEqualTo("PEN");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 37);
        calendar.set(Calendar.SECOND, 52);
        assertThat(creditNote.getFechaEmision()).isEqualTo(calendar.getTime());

        assertThat(creditNote.getDocumentoModifica()).isEqualTo("FF14-2");
        assertThat(creditNote.getTipoDocumentoModifica()).isEqualTo("FACTURA");
        assertThat(creditNote.getMotivoSustento()).isEqualTo("SIN OBSERVACIONES");

        assertThat(creditNote.getTributos().getTotalIgv()).isEqualTo(324F);
        assertThat(creditNote.getTributos().getTotalIsc()).isNull();
        assertThat(creditNote.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(creditNote.getInformacionAdicional().getTotalGravada()).isEqualTo(1_800F);
        assertThat(creditNote.getInformacionAdicional().getTotalInafecta()).isNull();
        assertThat(creditNote.getInformacionAdicional().getTotalExonerada()).isNull();
        assertThat(creditNote.getInformacionAdicional().getTotalGratuita()).isNull();

        assertThat(creditNote.getTotalVenta()).isEqualTo(2_124F);
        assertThat(creditNote.getTotalOtrosCargos()).isNull();
        assertThat(creditNote.getTotalDescuentoGlobal()).isNull();

        assertSupplier(creditNote.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(creditNote.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(creditNote.getCliente(),
                "10467793549",
                "RUC",
                "CARLOS ESTEBAN FERIIA VILA");
        assertThat(creditNote.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(creditNote.getDetalle().size()).isEqualTo(1);

        LineBean line = creditNote.getDetalle().get(0);

        assertLine(line,
                1F,
                "NIU",
                null,
                "CARRO",
                2_124F,
                2_124F,
                null,
                1_800F,
                null,
                324F,
                null
        );
    }

}