package org.clarksnut.mapper.document;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentMapperProviderFactoryTest {

    @Test
    public void getParsedDocumentProvider() throws Exception {
        DocumentMapperProviderFactory instance = DocumentMapperProviderFactory.getInstance();
        DocumentMapperProvider mapper = instance.getParsedDocumentProvider("basic", "Invoice");

        assertThat(mapper).isNotNull();
        assertThat(mapper.getGroup()).isEqualTo("basic");
        assertThat(mapper.getSupportedDocumentType()).isEqualTo("Invoice");
    }

}