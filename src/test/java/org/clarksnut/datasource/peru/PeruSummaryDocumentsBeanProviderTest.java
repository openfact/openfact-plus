package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.peru.beans.SummaryLineBean;
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
public class PeruSummaryDocumentsBeanProviderTest extends PeruBeanUtilsTest {

    @Mock
    private XmlUBLFileModel file;

    @Test
    public void getDatasourceRC_20170227_00001() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/peru/summarydocuments/RC-20170227-00001.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruSummaryDocumentsDatasourceProvider datasourceProvider = new PeruSummaryDocumentsDatasourceProvider();
        PeruSummaryDocumentsDatasource summaryDocuments = (PeruSummaryDocumentsDatasource) datasourceProvider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(summaryDocuments).isNotNull();
        assertThat(summaryDocuments.getIdAsignado()).isEqualTo("RC-20170227-00001");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 27);
        assertThat(summaryDocuments.getFechaEmision()).isEqualTo(calendar.getTime());
        assertThat(summaryDocuments.getFechaGeneracion()).isEqualTo(calendar.getTime());

        assertSupplier(summaryDocuments.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");

        //Lines
        assertThat(summaryDocuments.getDetalle().size()).isEqualTo(5);

        SummaryLineBean line = summaryDocuments.getDetalle().get(3);

        assertThat(line.getTipoDocumento()).isEqualTo("NOTA DE CREDITO");
        assertThat(line.getDocumentoSerie()).isEqualTo("BB12");
        assertThat(line.getDocumentoNumeroInicio()).isEqualTo("1");
        assertThat(line.getDocumentoNumeroFin()).isEqualTo("1");

        assertThat(line.getInformacionAdicional().getTotalGravada()).isEqualTo(0F);
        assertThat(line.getInformacionAdicional().getTotalExonerada()).isEqualTo(250F);
        assertThat(line.getInformacionAdicional().getTotalInafecta()).isEqualTo(0F);

        assertThat(line.getTributos().getTotalIgv()).isEqualTo(0F);
        assertThat(line.getTributos().getTotalIsc()).isEqualTo(0F);
        assertThat(line.getTributos().getTotalOtrosTributos()).isEqualTo(null);

        assertThat(line.getTotalVenta()).isEqualTo(250F);
        assertThat(line.getTotalOtrosCargos()).isEqualTo(0F);
        assertThat(line.getMoneda()).isEqualTo("PEN");
    }

}