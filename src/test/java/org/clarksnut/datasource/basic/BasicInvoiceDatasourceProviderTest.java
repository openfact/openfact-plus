package org.clarksnut.datasource.basic;

import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BasicInvoiceDatasourceProviderTest {

    @Mock
    private XmlUBLFileModel file;

    @Test
    public void getDatasource() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/basic/invoice/FF14-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        BasicInvoiceDatasourceProvider provider = new BasicInvoiceDatasourceProvider();
        BasicInvoiceDatasource datasource = (BasicInvoiceDatasource) provider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(datasource).isNotNull();
    }

}