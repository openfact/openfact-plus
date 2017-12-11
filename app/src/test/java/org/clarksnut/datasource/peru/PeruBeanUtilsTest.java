package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.peru.beans.ClienteBean;
import org.clarksnut.datasource.peru.beans.LineBean;
import org.clarksnut.datasource.peru.beans.PostalAddressBean;
import org.clarksnut.datasource.peru.beans.ProveedorBean;

import static org.assertj.core.api.Assertions.assertThat;

public class PeruBeanUtilsTest {

    public void assertSupplier(ProveedorBean supplier, String assignedId, String documentType, String supplierName) {
        assertThat(supplier.getIdAssignado()).isEqualTo(assignedId);
        assertThat(supplier.getTipoDocumento()).isEqualTo(documentType);
        assertThat(supplier.getNombre()).isEqualTo(supplierName);
    }

    public void assertCustomer(ClienteBean customer, String assignedId, String documentType, String supplierName) {
        assertThat(customer.getIdAssignado()).isEqualTo(assignedId);
        assertThat(customer.getTipoDocumento()).isEqualTo(documentType);
        assertThat(customer.getNombre()).isEqualTo(supplierName);
    }

    public void assertPostalAddress(PostalAddressBean postalAddress,
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

    public void assertLine(LineBean line,
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
