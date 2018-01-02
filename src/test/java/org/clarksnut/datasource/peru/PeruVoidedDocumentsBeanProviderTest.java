package org.clarksnut.datasource.peru;

import org.clarksnut.datasource.peru.beans.VoidedLineBean;
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
public class PeruVoidedDocumentsBeanProviderTest extends PeruBeanUtilsTest {

    @Mock
    private DocumentModel document;

    @Mock
    private XmlFileModel file;

    @Test
    public void getDatasourceRA_20170223_00004() throws Exception {

        InputStream is = getClass().getResourceAsStream("/peru/document/voideddocuments/RA-20170223-00004.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PeruVoidedDocumentsBeanProvider datasourceProvider = new PeruVoidedDocumentsBeanProvider();
        VoidedDocumentsDatasource voidedDocuments = (VoidedDocumentsDatasource) datasourceProvider.getDatasource(this.document, this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(voidedDocuments).isNotNull();
        assertThat(voidedDocuments.getIdAsignado()).isEqualTo("RA-20170223-00004");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 23);
        assertThat(voidedDocuments.getFechaEmision()).isEqualTo(calendar.getTime());
        assertThat(voidedDocuments.getFechaGeneracion()).isEqualTo(calendar.getTime());

        assertSupplier(voidedDocuments.getProveedor(),
                "20494637074",
                "RUC",
                "AHREN CONTRATISTAS GENERALES S.A.C");

        //Lines
        assertThat(voidedDocuments.getDetalle().size()).isEqualTo(5);

        VoidedLineBean line = voidedDocuments.getDetalle().get(4);

        assertThat(line.getDocumentoSerie()).isEqualTo("FF11");
        assertThat(line.getDocumentoNumero()).isEqualTo("7");
        assertThat(line.getTipoDocumento()).isEqualTo("FACTURA");
        assertThat(line.getMotivoBaja()).isEqualTo("PRUEBA");
    }

}