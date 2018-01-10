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
public class PeruInvoiceDatasourceProviderTest extends PeruBeanUtilsTest {

    @Mock
    private XmlUBLFileModel file;

    @Test
    public void getDatasourceBB11_1() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/invoice/BB11-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        PeruInvoiceDatasource invoice = (PeruInvoiceDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(invoice).isNotNull();
        assertThat(invoice.getTipoDocumento()).isEqualTo("BOLETA");
        assertThat(invoice.getIdAsignado()).isEqualTo("BB11-1");
        assertThat(invoice.getMoneda()).isEqualTo("PEN");


        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 46);
        calendar.set(Calendar.SECOND, 47);
        assertThat(invoice.getFechaEmision()).isEqualTo(calendar.getTime());
        assertThat(invoice.getFechaVencimiento()).isNull();

        assertThat(invoice.getTributos().getTotalIgv()).isEqualTo(270F);
        assertThat(invoice.getTributos().getTotalIsc()).isNull();
        assertThat(invoice.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(invoice.getInformacionAdicional().getTotalGravada()).isEqualTo(1_500F);
        assertThat(invoice.getInformacionAdicional().getTotalInafecta()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalExonerada()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalGratuita()).isNull();

        assertSupplier(invoice.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(invoice.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(invoice.getCliente(),
                "46779354",
                "DNI",
                "CARLOS ESTEBAN FERIA VILA");
        assertThat(invoice.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(invoice.getDetalle().size()).isEqualTo(4);

        LineBean line = invoice.getDetalle().get(3);

        assertLine(line,
                4F,
                "NIU",
                null,
                "MALETIN",
                200F,
                0F,
                236F,
                800F,
                null,
                144F,
                null
        );
    }

    @Test
    public void getDatasourceBB12_1() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/invoice/BB12-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        PeruInvoiceDatasource invoice = (PeruInvoiceDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(invoice).isNotNull();
        assertThat(invoice.getTipoDocumento()).isEqualTo("BOLETA");
        assertThat(invoice.getIdAsignado()).isEqualTo("BB12-1");
        assertThat(invoice.getMoneda()).isEqualTo("PEN");


        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 9);
        calendar.set(Calendar.SECOND, 14);
        assertThat(invoice.getFechaEmision()).isEqualTo(calendar.getTime());
        assertThat(invoice.getFechaVencimiento()).isNull();

        assertThat(invoice.getTributos().getTotalIgv()).isNull();
        assertThat(invoice.getTributos().getTotalIsc()).isNull();
        assertThat(invoice.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(invoice.getInformacionAdicional().getTotalGravada()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalInafecta()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalExonerada()).isEqualTo(250F);
        assertThat(invoice.getInformacionAdicional().getTotalGratuita()).isNull();

        assertSupplier(invoice.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(invoice.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(invoice.getCliente(),
                "46779354",
                "DNI",
                "CARLOS ESTEBAN FERIA VILA");
        assertThat(invoice.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(invoice.getDetalle().size()).isEqualTo(2);

        LineBean line = invoice.getDetalle().get(1);

        assertLine(line,
                2F,
                "NIU",
                null,
                "CINSTA AISLANTE",
                100F,
                100F,
                null,
                200F,
                null,
                0F,
                null
        );
    }

    @Test
    public void getDatasourceBB13_1() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/invoice/BB13-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        PeruInvoiceDatasource invoice = (PeruInvoiceDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(invoice).isNotNull();
        assertThat(invoice.getTipoDocumento()).isEqualTo("BOLETA");
        assertThat(invoice.getIdAsignado()).isEqualTo("BB13-1");
        assertThat(invoice.getMoneda()).isEqualTo("PEN");


        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 21);
        calendar.set(Calendar.SECOND, 59);
        assertThat(invoice.getFechaEmision()).isEqualTo(calendar.getTime());
        assertThat(invoice.getFechaVencimiento()).isNull();

        assertThat(invoice.getTributos().getTotalIgv()).isNull();
        assertThat(invoice.getTributos().getTotalIsc()).isNull();
        assertThat(invoice.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(invoice.getInformacionAdicional().getTotalGravada()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalInafecta()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalExonerada()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalGratuita()).isEqualTo(7_819F);

        assertSupplier(invoice.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(invoice.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(invoice.getCliente(),
                "46779354",
                "DNI",
                "CARLOS ESTEBAN FERIA VILA");
        assertThat(invoice.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(invoice.getDetalle().size()).isEqualTo(7);

        LineBean line = invoice.getDetalle().get(0);

        assertLine(line,
                1F,
                "NIU",
                null,
                "PRODUCTO1",
                50F,
                0F,
                59F,
                50F,
                null,
                9F,
                null
        );
    }

    @Test
    public void getDatasourceBB14_1() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/invoice/BB14-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        PeruInvoiceDatasource invoice = (PeruInvoiceDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(invoice).isNotNull();
        assertThat(invoice.getTipoDocumento()).isEqualTo("BOLETA");
        assertThat(invoice.getIdAsignado()).isEqualTo("BB14-1");
        assertThat(invoice.getMoneda()).isEqualTo("PEN");


        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 33);
        calendar.set(Calendar.SECOND, 51);
        assertThat(invoice.getFechaEmision()).isEqualTo(calendar.getTime());
        assertThat(invoice.getFechaVencimiento()).isNull();

        assertThat(invoice.getTributos().getTotalIgv()).isEqualTo(3_118.5F);
        assertThat(invoice.getTributos().getTotalIsc()).isNull();
        assertThat(invoice.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(invoice.getInformacionAdicional().getTotalGravada()).isEqualTo(17_325F);
        assertThat(invoice.getInformacionAdicional().getTotalInafecta()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalExonerada()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalGratuita()).isNull();

        assertSupplier(invoice.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(invoice.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(invoice.getCliente(),
                "46779354",
                "DNI",
                "CARLOS ESTEBAN FERIA VILA");
        assertThat(invoice.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(invoice.getDetalle().size()).isEqualTo(10);

        LineBean line = invoice.getDetalle().get(4);

        assertLine(line,
                5F,
                "NIU",
                null,
                "PRODUCTO5",
                250F,
                295F,
                null,
                1_250F,
                null,
                225F,
                null
        );
    }

    @Test
    public void getDatasourceFF11_3() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/invoice/FF11-00000003.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        PeruInvoiceDatasource invoice = (PeruInvoiceDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(invoice).isNotNull();
        assertThat(invoice.getTipoDocumento()).isEqualTo("FACTURA");
        assertThat(invoice.getIdAsignado()).isEqualTo("FF11-00000003");
        assertThat(invoice.getMoneda()).isEqualTo("PEN");


        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 46);
        calendar.set(Calendar.SECOND, 20);
        assertThat(invoice.getFechaEmision()).isEqualTo(calendar.getTime());
        assertThat(invoice.getFechaVencimiento()).isNull();

        assertThat(invoice.getTributos().getTotalIgv()).isEqualTo(21.15F);
        assertThat(invoice.getTributos().getTotalIsc()).isNull();
        assertThat(invoice.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(invoice.getInformacionAdicional().getTotalGravada()).isEqualTo(117.5F);
        assertThat(invoice.getInformacionAdicional().getTotalInafecta()).isEqualTo(0F);
        assertThat(invoice.getInformacionAdicional().getTotalExonerada()).isEqualTo(0F);
        assertThat(invoice.getInformacionAdicional().getTotalGratuita()).isEqualTo(0F);

        assertSupplier(invoice.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(invoice.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(invoice.getCliente(),
                "10467793549",
                "RUC",
                "CARLOS ESTEBAN FERIA VILA");
        assertThat(invoice.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(invoice.getDetalle().size()).isEqualTo(3);

        LineBean line = invoice.getDetalle().get(2);

        assertLine(line,
                12F,
                "NIU",
                null,
                "RESALTADOR",
                5F,
                5.9F,
                null,
                60F,
                null,
                10.8F,
                null
        );
    }

    @Test
    public void getDatasourceFF12_3() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/invoice/FF12-3.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        PeruInvoiceDatasource invoice = (PeruInvoiceDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(invoice).isNotNull();
        assertThat(invoice.getTipoDocumento()).isEqualTo("FACTURA");
        assertThat(invoice.getIdAsignado()).isEqualTo("FF12-3");
        assertThat(invoice.getMoneda()).isEqualTo("PEN");


        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 50);
        calendar.set(Calendar.SECOND, 3);
        assertThat(invoice.getFechaEmision()).isEqualTo(calendar.getTime());
        assertThat(invoice.getFechaVencimiento()).isNull();

        assertThat(invoice.getTributos().getTotalIgv()).isEqualTo(0F);
        assertThat(invoice.getTributos().getTotalIsc()).isNull();
        assertThat(invoice.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(invoice.getInformacionAdicional().getTotalGravada()).isEqualTo(0F);
        assertThat(invoice.getInformacionAdicional().getTotalInafecta()).isEqualTo(0F);
        assertThat(invoice.getInformacionAdicional().getTotalExonerada()).isEqualTo(1_800F);
        assertThat(invoice.getInformacionAdicional().getTotalGratuita()).isEqualTo(0F);

        assertSupplier(invoice.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(invoice.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(invoice.getCliente(),
                "10467793549",
                "RUC",
                "CARLOS ESTEBAN FERIA VIL");
        assertThat(invoice.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(invoice.getDetalle().size()).isEqualTo(1);

        LineBean line = invoice.getDetalle().get(0);

        assertLine(line,
                1F,
                "NIU",
                null,
                "CELULAR",
                1800F,
                1800F,
                null,
                1800F,
                null,
                0F,
                null
        );
    }

    @Test
    public void getDatasourceFF13_2() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/invoice/FF13-3.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        PeruInvoiceDatasource invoice = (PeruInvoiceDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(invoice).isNotNull();
        assertThat(invoice.getTipoDocumento()).isEqualTo("FACTURA");
        assertThat(invoice.getIdAsignado()).isEqualTo("FF13-3");
        assertThat(invoice.getMoneda()).isEqualTo("PEN");


        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 16);
        calendar.set(Calendar.SECOND, 51);
        assertThat(invoice.getFechaEmision()).isEqualTo(calendar.getTime());
        assertThat(invoice.getFechaVencimiento()).isNull();

        assertThat(invoice.getTributos().getTotalIgv()).isNull();
        assertThat(invoice.getTributos().getTotalIsc()).isNull();
        assertThat(invoice.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(invoice.getInformacionAdicional().getTotalGravada()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalInafecta()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalExonerada()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalGratuita()).isEqualTo(7_819F);

        assertSupplier(invoice.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(invoice.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(invoice.getCliente(),
                "10467793549",
                "RUC",
                "CARLOS ESTEBAN FERIA VILA");
        assertThat(invoice.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(invoice.getDetalle().size()).isEqualTo(7);

        LineBean line = invoice.getDetalle().get(6);

        assertLine(line,
                7F,
                "NIU",
                null,
                "PAPEL BOND",
                350F,
                0F,
                350F,
                2_450F,
                null,
                0F,
                null
        );
    }

    @Test
    public void getDatasourceFF14_1() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/invoice/FF14-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        PeruInvoiceDatasource invoice = (PeruInvoiceDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(invoice).isNotNull();
        assertThat(invoice.getTipoDocumento()).isEqualTo("FACTURA");
        assertThat(invoice.getIdAsignado()).isEqualTo("FF14-1");
        assertThat(invoice.getMoneda()).isEqualTo("PEN");


        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 29);
        calendar.set(Calendar.SECOND, 18);
        assertThat(invoice.getFechaEmision()).isEqualTo(calendar.getTime());
        assertThat(invoice.getFechaVencimiento()).isNull();

        assertThat(invoice.getTributos().getTotalIgv()).isEqualTo(40.5F);
        assertThat(invoice.getTributos().getTotalIsc()).isNull();
        assertThat(invoice.getTributos().getTotalOtrosTributos()).isNull();

        assertThat(invoice.getInformacionAdicional().getTotalGravada()).isEqualTo(225F);
        assertThat(invoice.getInformacionAdicional().getTotalInafecta()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalExonerada()).isNull();
        assertThat(invoice.getInformacionAdicional().getTotalGratuita()).isNull();

        assertSupplier(invoice.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(invoice.getProveedor().getDireccion(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");
        assertCustomer(invoice.getCliente(),
                "10467793549",
                "RUC",
                "CARLOS ESTEBAN FERIA VILA");
        assertThat(invoice.getCliente().getDireccion()).isNull();

        // Lines
        assertThat(invoice.getDetalle().size()).isEqualTo(2);

        LineBean line = invoice.getDetalle().get(1);

        assertLine(line,
                2F,
                "NIU",
                null,
                "CARTAPASIO",
                100F,
                118F,
                null,
                200F,
                null,
                36F,
                null
        );
    }


}