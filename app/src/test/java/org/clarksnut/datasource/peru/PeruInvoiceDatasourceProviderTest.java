package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.peru.beans.*;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
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
public class PeruInvoiceDatasourceProviderTest {

    @Mock
    private DocumentModel document;

    @Mock
    private XmlFileModel file;

    @Test
    public void getDatasourceBB11_1() throws Exception {
        InputStream is = getClass().getResourceAsStream("/peru/document/invoice/BB11-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        InvoiceDatasource invoice = (InvoiceDatasource) datasourceProvider.getDatasource(this.document, this.file);

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

        assertThat(invoice.getTotalIgv()).isEqualTo(270F);
        assertThat(invoice.getTotalIsc()).isNull();
        assertThat(invoice.getTotalOtrosTributos()).isNull();

        assertThat(invoice.getTotalGravada()).isEqualTo(1_500F);
        assertThat(invoice.getTotalInafecta()).isNull();
        assertThat(invoice.getTotalExonerada()).isNull();
        assertThat(invoice.getTotalGratuita()).isNull();

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
        InputStream is = getClass().getResourceAsStream("/peru/document/invoice/BB12-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        InvoiceDatasource invoice = (InvoiceDatasource) datasourceProvider.getDatasource(this.document, this.file);

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

        assertThat(invoice.getTotalIgv()).isNull();
        assertThat(invoice.getTotalIsc()).isNull();
        assertThat(invoice.getTotalOtrosTributos()).isNull();

        assertThat(invoice.getTotalGravada()).isNull();
        assertThat(invoice.getTotalInafecta()).isNull();
        assertThat(invoice.getTotalExonerada()).isEqualTo(250F);
        assertThat(invoice.getTotalGratuita()).isNull();

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
        InputStream is = getClass().getResourceAsStream("/peru/document/invoice/BB13-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        InvoiceDatasource invoice = (InvoiceDatasource) datasourceProvider.getDatasource(this.document, this.file);

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

        assertThat(invoice.getTotalIgv()).isNull();
        assertThat(invoice.getTotalIsc()).isNull();
        assertThat(invoice.getTotalOtrosTributos()).isNull();

        assertThat(invoice.getTotalGravada()).isNull();
        assertThat(invoice.getTotalInafecta()).isNull();
        assertThat(invoice.getTotalExonerada()).isNull();
        assertThat(invoice.getTotalGratuita()).isEqualTo(7_819F);

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
        InputStream is = getClass().getResourceAsStream("/peru/document/invoice/BB14-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        InvoiceDatasource invoice = (InvoiceDatasource) datasourceProvider.getDatasource(this.document, this.file);

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

        assertThat(invoice.getTotalIgv()).isEqualTo(3_118.5F);
        assertThat(invoice.getTotalIsc()).isNull();
        assertThat(invoice.getTotalOtrosTributos()).isNull();

        assertThat(invoice.getTotalGravada()).isEqualTo(17_325F);
        assertThat(invoice.getTotalInafecta()).isNull();
        assertThat(invoice.getTotalExonerada()).isNull();
        assertThat(invoice.getTotalGratuita()).isNull();

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
        InputStream is = getClass().getResourceAsStream("/peru/document/invoice/FF11-00000003.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        InvoiceDatasource invoice = (InvoiceDatasource) datasourceProvider.getDatasource(this.document, this.file);

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

        assertThat(invoice.getTotalIgv()).isEqualTo(21.15F);
        assertThat(invoice.getTotalIsc()).isNull();
        assertThat(invoice.getTotalOtrosTributos()).isNull();

        assertThat(invoice.getTotalGravada()).isEqualTo(117.5F);
        assertThat(invoice.getTotalInafecta()).isEqualTo(0F);
        assertThat(invoice.getTotalExonerada()).isEqualTo(0F);
        assertThat(invoice.getTotalGratuita()).isEqualTo(0F);

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
        InputStream is = getClass().getResourceAsStream("/peru/document/invoice/FF12-3.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        InvoiceDatasource invoice = (InvoiceDatasource) datasourceProvider.getDatasource(this.document, this.file);

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

        assertThat(invoice.getTotalIgv()).isEqualTo(0F);
        assertThat(invoice.getTotalIsc()).isNull();
        assertThat(invoice.getTotalOtrosTributos()).isNull();

        assertThat(invoice.getTotalGravada()).isEqualTo(0F);
        assertThat(invoice.getTotalInafecta()).isEqualTo(0F);
        assertThat(invoice.getTotalExonerada()).isEqualTo(1_800F);
        assertThat(invoice.getTotalGratuita()).isEqualTo(0F);

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
        InputStream is = getClass().getResourceAsStream("/peru/document/invoice/FF13-3.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        InvoiceDatasource invoice = (InvoiceDatasource) datasourceProvider.getDatasource(this.document, this.file);

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

        assertThat(invoice.getTotalIgv()).isNull();
        assertThat(invoice.getTotalIsc()).isNull();
        assertThat(invoice.getTotalOtrosTributos()).isNull();

        assertThat(invoice.getTotalGravada()).isNull();
        assertThat(invoice.getTotalInafecta()).isNull();
        assertThat(invoice.getTotalExonerada()).isNull();
        assertThat(invoice.getTotalGratuita()).isEqualTo(7_819F);

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
        InputStream is = getClass().getResourceAsStream("/peru/document/invoice/FF14-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        InvoiceDatasource invoice = (InvoiceDatasource) datasourceProvider.getDatasource(this.document, this.file);

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

        assertThat(invoice.getTotalIgv()).isEqualTo(40.5F);
        assertThat(invoice.getTotalIsc()).isNull();
        assertThat(invoice.getTotalOtrosTributos()).isNull();

        assertThat(invoice.getTotalGravada()).isEqualTo(225F);
        assertThat(invoice.getTotalInafecta()).isNull();
        assertThat(invoice.getTotalExonerada()).isNull();
        assertThat(invoice.getTotalGratuita()).isNull();

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

    private void assertSupplier(ProveedorBean supplier, String assignedId, String documentType, String supplierName) {
        assertThat(supplier.getIdAssignado()).isEqualTo(assignedId);
        assertThat(supplier.getTipoDocumento()).isEqualTo(documentType);
        assertThat(supplier.getNombre()).isEqualTo(supplierName);
    }

    private void assertCustomer(ClienteBean customer, String assignedId, String documentType, String supplierName) {
        assertThat(customer.getIdAssignado()).isEqualTo(assignedId);
        assertThat(customer.getTipoDocumento()).isEqualTo(documentType);
        assertThat(customer.getNombre()).isEqualTo(supplierName);
    }

    private void assertPostalAddress(PostalAddressBean postalAddress,
                                     String id,
                                     String streetName,
                                     String citySubdivisionName,
                                     String cityName,
                                     String countrySubentity,
                                     String countryIdentificationCode) {
        assertThat(postalAddress.getId()).isEqualTo(id);
        assertThat(postalAddress.getStreetName()).isEqualTo(streetName);
        assertThat(postalAddress.getCitySubdivisionName()).isEqualTo(citySubdivisionName);
        assertThat(postalAddress.getCityName()).isEqualTo(cityName);
        assertThat(postalAddress.getCountrySubentity()).isEqualTo(countrySubentity);
        assertThat(postalAddress.getCountryIdentificationCode()).isEqualTo(countryIdentificationCode);
    }

    private void assertLine(LineBean line,
                            Float cantidad,
                            String unidadMedida,
                            String codigoProducto,
                            String descripcion,
                            Float precioUnitario,
                            Float precioVentaUnitario,
                            Float valorReferencialUnitarioEnOperacionesNoOnerosas,
                            Float totalValorVenta,
                            Float totalDescuento,
                            Float totalIgv,
                            Float totalIsc) {
        assertThat(line.getCantidad()).isEqualTo(cantidad);
        assertThat(line.getUnidadMedida()).isEqualTo(unidadMedida);
        assertThat(line.getCodidoProducto()).isEqualTo(codigoProducto);
        assertThat(line.getDescripcion()).isEqualTo(descripcion);

        assertThat(line.getPrecioUnitario()).isEqualTo(precioUnitario);
        assertThat(line.getPrecioVentaUnitario()).isEqualTo(precioVentaUnitario);
        assertThat(line.getValorReferencialUnitarioEnOperacionesNoOnerosas()).isEqualTo(valorReferencialUnitarioEnOperacionesNoOnerosas);
        assertThat(line.getTotalValorVenta()).isEqualTo(totalValorVenta);
        assertThat(line.getTotalDescuento()).isEqualTo(totalDescuento);

        assertThat(line.getTotalIgv()).isEqualTo(totalIgv);
        assertThat(line.getTotalIsc()).isEqualTo(totalIsc);
    }

}