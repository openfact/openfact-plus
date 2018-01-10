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
public class PeruDebitNoteBeanProviderTest extends PeruBeanUtilsTest {

    @Mock
    private XmlUBLFileModel file;
    
    @Test
    public void getDatasourceFF11_5() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/debitnote/FF11-5.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruDebitNoteDatasourceProvider datasourceProvider = new PeruDebitNoteDatasourceProvider();
        PeruDebitNoteDatasource debitNote = (PeruDebitNoteDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(debitNote).isNotNull();
        assertThat(debitNote.getIdAsignado()).isEqualTo("FF11-5");
        assertThat(debitNote.getTipoNotaDebito()).isEqualTo("INTERES POR MORA");
        assertThat(debitNote.getMoneda()).isEqualTo("PEN");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 31);
        calendar.set(Calendar.SECOND, 27);
        assertThat(debitNote.getFechaEmision()).isEqualTo(calendar.getTime());

        assertThat(debitNote.getDocumentoModifica()).isEqualTo("FF11-00000004");
        assertThat(debitNote.getTipoDocumentoModifica()).isEqualTo("FACTURA");
        assertThat(debitNote.getMotivoSustento()).isEqualTo("SIN OBSERVACIONES");

        assertThat(debitNote.getTributos().getTotalIgv()).isEqualTo(57.24F);
        assertThat(debitNote.getTributos().getTotalIsc()).isNull();
        assertThat(debitNote.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(debitNote.getInformacionAdicional().getTotalGravada()).isEqualTo(318F);
        assertThat(debitNote.getInformacionAdicional().getTotalInafecta()).isNull();
        assertThat(debitNote.getInformacionAdicional().getTotalExonerada()).isNull();
        assertThat(debitNote.getInformacionAdicional().getTotalGratuita()).isNull();

        assertThat(debitNote.getTotalVenta()).isEqualTo(375.24F);
        assertThat(debitNote.getTotalOtrosCargos()).isNull();
        assertThat(debitNote.getTotalDescuentoGlobal()).isEqualTo(0F);

        assertSupplier(debitNote.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(debitNote.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(debitNote.getCliente(),
                "10467793549",
                "RUC",
                "CARLOS ESTEBAN FERIA VILA");
        assertThat(debitNote.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(debitNote.getDetalle().size()).isEqualTo(2);

        LineBean line = debitNote.getDetalle().get(1);

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
        InputStream is = getClass().getResourceAsStream("/ubl/peru/debitnote/FF12-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruDebitNoteDatasourceProvider datasourceProvider = new PeruDebitNoteDatasourceProvider();
        PeruDebitNoteDatasource debitNote = (PeruDebitNoteDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(debitNote).isNotNull();
        assertThat(debitNote.getIdAsignado()).isEqualTo("FF12-1");
        assertThat(debitNote.getTipoNotaDebito()).isEqualTo("INTERES POR MORA");
        assertThat(debitNote.getMoneda()).isEqualTo("PEN");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 22);
        calendar.set(Calendar.SECOND, 47);
        assertThat(debitNote.getFechaEmision()).isEqualTo(calendar.getTime());

        assertThat(debitNote.getDocumentoModifica()).isEqualTo("FF12-3");
        assertThat(debitNote.getTipoDocumentoModifica()).isEqualTo("FACTURA");
        assertThat(debitNote.getMotivoSustento()).isEqualTo("SIN OBSERVACIONES");

        assertThat(debitNote.getTributos().getTotalIgv()).isNull();
        assertThat(debitNote.getTributos().getTotalIsc()).isNull();
        assertThat(debitNote.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(debitNote.getInformacionAdicional().getTotalGravada()).isNull();
        assertThat(debitNote.getInformacionAdicional().getTotalInafecta()).isNull();
        assertThat(debitNote.getInformacionAdicional().getTotalExonerada()).isEqualTo(1_800F);
        assertThat(debitNote.getInformacionAdicional().getTotalGratuita()).isNull();

        assertThat(debitNote.getTotalVenta()).isEqualTo(1_800F);
        assertThat(debitNote.getTotalOtrosCargos()).isNull();
        assertThat(debitNote.getTotalDescuentoGlobal()).isNull();

        assertSupplier(debitNote.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(debitNote.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(debitNote.getCliente(),
                "10467793549",
                "RUC",
                "CARLOS ESTEBAN FERIA VILA");
        assertThat(debitNote.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(debitNote.getDetalle().size()).isEqualTo(1);

        LineBean line = debitNote.getDetalle().get(0);

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
        InputStream is = getClass().getResourceAsStream("/ubl/peru/debitnote/FF14-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruDebitNoteDatasourceProvider datasourceProvider = new PeruDebitNoteDatasourceProvider();
        PeruDebitNoteDatasource debitNote = (PeruDebitNoteDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(debitNote).isNotNull();
        assertThat(debitNote.getIdAsignado()).isEqualTo("FF14-1");
        assertThat(debitNote.getTipoNotaDebito()).isEqualTo("INTERES POR MORA");
        assertThat(debitNote.getMoneda()).isEqualTo("PEN");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 41);
        calendar.set(Calendar.SECOND, 38);
        assertThat(debitNote.getFechaEmision()).isEqualTo(calendar.getTime());

        assertThat(debitNote.getDocumentoModifica()).isEqualTo("FF14-2");
        assertThat(debitNote.getTipoDocumentoModifica()).isEqualTo("FACTURA");
        assertThat(debitNote.getMotivoSustento()).isEqualTo("SIN OBSERVACIONES");

        assertThat(debitNote.getTributos().getTotalIgv()).isEqualTo(324F);
        assertThat(debitNote.getTributos().getTotalIsc()).isNull();
        assertThat(debitNote.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(debitNote.getInformacionAdicional().getTotalGravada()).isEqualTo(1_800F);
        assertThat(debitNote.getInformacionAdicional().getTotalInafecta()).isNull();
        assertThat(debitNote.getInformacionAdicional().getTotalExonerada()).isNull();
        assertThat(debitNote.getInformacionAdicional().getTotalGratuita()).isNull();

        assertThat(debitNote.getTotalVenta()).isEqualTo(2_124F);
        assertThat(debitNote.getTotalOtrosCargos()).isNull();
        assertThat(debitNote.getTotalDescuentoGlobal()).isNull();

        assertSupplier(debitNote.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(debitNote.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(debitNote.getCliente(),
                "10467793549",
                "RUC",
                "CARLOS ESTEBAN FERIIA VILA");
        assertThat(debitNote.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(debitNote.getDetalle().size()).isEqualTo(1);

        LineBean line = debitNote.getDetalle().get(0);

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