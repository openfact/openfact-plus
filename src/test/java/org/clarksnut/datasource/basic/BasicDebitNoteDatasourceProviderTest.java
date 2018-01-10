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
public class BasicDebitNoteDatasourceProviderTest {

    @Mock
    private XmlUBLFileModel file;

    @Test
    public void getDatasource() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ubl/basic/debitnote/FF14-1.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        BasicDebitNoteDatasourceProvider provider = new BasicDebitNoteDatasourceProvider();
        BasicDebitNoteDatasource datasource = (BasicDebitNoteDatasource) provider.getDatasource(this.file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();

        assertThat(datasource).isNotNull();
    }

}