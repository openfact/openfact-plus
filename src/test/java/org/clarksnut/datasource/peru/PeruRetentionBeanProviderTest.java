package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.peru.beans.RetentionLineBean;
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
public class PeruRetentionBeanProviderTest extends PeruBeanUtilsTest {

    @Mock
    private XmlUBLFileModel file;

    @Test
    public void getDatasourceP001_123() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/retention/R001-123.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruRetentionDatasourceProvider datasourceProvider = new PeruRetentionDatasourceProvider();
        PeruRetentionDatasource perception = (PeruRetentionDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(perception).isNotNull();
        assertThat(perception.getIdAsignado()).isEqualTo("R001-123");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        assertThat(perception.getFechaEmision()).isEqualTo(calendar.getTime());

        assertSupplier(perception.getEmisor(),
                "20382567855",
                "RUC",
                "ENTERPRISE SOLUTIONS S.A.");
        assertPostalAddress(perception.getEmisor().getDireccion(),
                "150136",
                "CALLE TIAHUANACO 146",
                "URB. SANTA FELICIA",
                "LIMA",
                "LIMA",
                "PE");

        assertCustomer(perception.getProveedor(),
                "20101295673",
                "RUC",
                "FABRICACIONES ONUR SAC");
        assertPostalAddress(perception.getProveedor().getDireccion(),
                "150101",
                "AV. ABANCAY 1023",
                "CERCADO DE LIMA",
                "LIMA",
                "LIMA",
                "PE");

        //Lines
        assertThat(perception.getDetalle().size()).isEqualTo(1);

        RetentionLineBean line = perception.getDetalle().get(0);

        assertThat(line.getTipoDocumentoRelacionado()).isEqualTo("FACTURA");
        assertThat(line.getDocumentoRelacionado()).isEqualTo("F001-540");
        assertThat(line.getImporteTotalDocumentoRelacionado()).isEqualTo(1_180F);
        assertThat(line.getMonedaDocumentoRelacionado()).isEqualTo("PEN");

        assertThat(line.getImporteTotalAPagar()).isEqualTo(1_144.60F);
        assertThat(line.getMonedaImporteTotalAPagar()).isEqualTo("PEN");

        assertThat(line.getImporteRetenido()).isEqualTo(35.40F);
        assertThat(line.getMonedaImporteRetenido()).isEqualTo("PEN");
        assertThat(line.getImporteTotalAPagar()).isEqualTo(1_144.60F);
        assertThat(line.getMonedaImporteTotalAPagar()).isEqualTo("PEN");
    }

}