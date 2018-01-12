package org.clarksnut.files.uncompress;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class UncompressFileProviderFactoryTest {

    @Test
    public void getUncompressFileProvider() throws Exception {
        UncompressFileProviderFactory instance = UncompressFileProviderFactory.getInstance();
        UncompressFileProvider provider = instance.getUncompressFileProvider("zip");

        assertThat(provider).isNotNull();
        assertThat(provider.getFileExtensionSupported()).isEqualTo("zip");
    }

}