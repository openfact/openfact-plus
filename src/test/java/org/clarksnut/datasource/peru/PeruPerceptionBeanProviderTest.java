package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.peru.beans.PerceptionLineBean;
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
public class PeruPerceptionBeanProviderTest extends PeruBeanUtilsTest {

    @Mock
    private XmlUBLFileModel file;

    @Test
    public void getDatasourceP001_123() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/perception/P001-123.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruPerceptionDatasourceProvider datasourceProvider = new PeruPerceptionDatasourceProvider();
        PeruPerceptionDatasource perception = (PeruPerceptionDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(perception).isNotNull();
        assertThat(perception.getIdAsignado()).isEqualTo("P001-123");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2015);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 23);
        assertThat(perception.getFechaEmision()).isEqualTo(calendar.getTime());

        assertSupplier(perception.getEmisor(),
                "20100113612",
                "RUC",
                "K&G Asociados S. A.");
        assertPostalAddress(perception.getEmisor().getDireccion(),
                "150114",
                "AV. LOS OLIVOS 767",
                "URB. SANTA FELICIA",
                "LIMA",
                "LIMA",
                "PE");

        assertCustomer(perception.getCliente(),
                "20546772439",
                "RUC",
                "CIA. DE CONSULTORIA Y PLANEAMIENTO S.A.C.");
        assertPostalAddress(perception.getCliente().getDireccion(),
                "150130",
                "CAL. CALLE MORELLI 181 INT. P-2",
                "",
                "LIMA",
                "LIMA",
                "PE");

        //Lines
        assertThat(perception.getDetalle().size()).isEqualTo(2);

        PerceptionLineBean line = perception.getDetalle().get(1);

        assertThat(line.getTipoDocumentoRelacionado()).isEqualTo("FACTURA");
        assertThat(line.getDocumentoRelacionado()).isEqualTo("E001-540");
        assertThat(line.getImporteTotalDocumentoRelacionado()).isEqualTo(1_000F);
        assertThat(line.getMonedaDocumentoRelacionado()).isEqualTo("PEN");

        assertThat(line.getImporteCobro()).isEqualTo(1_000F);
        assertThat(line.getMonedaCobro()).isEqualTo("PEN");

        assertThat(line.getImportePercibido()).isEqualTo(20F);
        assertThat(line.getMonedaImportePercibido()).isEqualTo("PEN");
        assertThat(line.getImporteTotalACobrar()).isEqualTo(1_020F);
        assertThat(line.getMonedaImporteTotalACobrar()).isEqualTo("PEN");
    }

}