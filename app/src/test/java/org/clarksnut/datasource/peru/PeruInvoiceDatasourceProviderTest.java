package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.peru.beans.CustomerBean;
import org.clarksnut.datasource.peru.beans.InvoiceBean;
import org.clarksnut.datasource.peru.beans.PostalAddressBean;
import org.clarksnut.datasource.peru.beans.SupplierBean;
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
    public void getDatasource() throws Exception {
        InputStream is = getClass().getResourceAsStream("/peru/document/invoice/FF14-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruInvoiceDatasourceProvider datasourceProvider = new PeruInvoiceDatasourceProvider();
        InvoiceBean invoice = (InvoiceBean) datasourceProvider.getDatasource(this.document, this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(invoice).isNotNull();
        assertThat(invoice.getInvoiceType()).isEqualTo("FACTURA");
        assertThat(invoice.getAssignedId()).isEqualTo("FF14-1");
        assertThat(invoice.getCurrency()).isEqualTo("PEN");


        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 29);
        calendar.set(Calendar.SECOND, 18);
        assertThat(invoice.getIssueDate()).isEqualTo(calendar.getTime());
        assertThat(invoice.getPaymentDue()).isNull();

        assertThat(invoice.getTotalIgv()).isEqualTo(40.5F);
        assertThat(invoice.getTotalIsc()).isNull();
        assertThat(invoice.getTotalOtrosTributos()).isNull();

        assertThat(invoice.getTotalGravada()).isEqualTo(225F);
        assertThat(invoice.getTotalInafecta()).isNull();
        assertThat(invoice.getTotalExonerada()).isNull();
        assertThat(invoice.getTotalGratuita()).isNull();

        assertSupplier(invoice.getSupplier(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");
        assertPostalAddress(invoice.getSupplier().getPostalAddress(),
                "050101",
                "Mza. A Lote. 3 A.v. Santa Teresa",
                "Huamanga",
                "Ayacucho",
                "Ayacucho",
                "PE");

        assertCustomer(invoice.getCustomer(),
                "10467793549",
                "RUC",
                "CARLOS ESTEBAN FERIA VILA");
        assertThat(invoice.getCustomer().getPostalAddress()).isNull();
    }

    private void assertSupplier(SupplierBean supplier, String assignedId, String documentType, String supplierName) {
        assertThat(supplier.getSupplierAssignedId()).isEqualTo(assignedId);
        assertThat(supplier.getSupplierDocumentType()).isEqualTo(documentType);
        assertThat(supplier.getSupplierName()).isEqualTo(supplierName);
    }

    private void assertCustomer(CustomerBean customer, String assignedId, String documentType, String supplierName) {
        assertThat(customer.getCustomerAssignedId()).isEqualTo(assignedId);
        assertThat(customer.getCustomerDocumentType()).isEqualTo(documentType);
        assertThat(customer.getCustomerName()).isEqualTo(supplierName);
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

}