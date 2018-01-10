package org.clarksnut.datasource;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class DatasourceFactoryTest {

    @Test
    public void getInstance() throws Exception {
        DatasourceFactory instance = DatasourceFactory.getInstance();
        DatasourceProvider provider = instance.getDatasourceProvider("BasicInvoiceDS");

        assertThat(provider).isNotNull();
        assertThat(provider.getName()).isEqualTo("BasicInvoiceDS");
    }

}