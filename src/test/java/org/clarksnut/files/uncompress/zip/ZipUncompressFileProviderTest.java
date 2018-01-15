package org.clarksnut.files.uncompress.zip;

import org.apache.commons.io.IOUtils;
import org.clarksnut.files.uncompress.FileEntryModel;
import org.clarksnut.files.uncompress.UncompressFileProviderFacade;
import org.clarksnut.files.uncompress.UncompressedFileModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ZipUncompressFileProviderTest {

//    @Test
//    public void uncompress() throws Exception {
//        String filename = "file.zip";
//        InputStream is = getClass().getClassLoader().getResourceAsStream("files/" + filename);
//
//        byte[] bytes = IOUtils.toByteArray(is);
//
//        UncompressFileProviderFacade facade = new UncompressFileProviderFacade();
//        UncompressedFileModel uncompressedFile = facade.uncompress(filename, bytes);
//
//        assertThat(uncompressedFile).isNotNull();
//        assertThat(uncompressedFile.isCompressedFile()).isEqualTo(true);
//        assertThat(uncompressedFile.getFileEntry().getFilename()).isEqualTo(filename);
//        assertThat(uncompressedFile.getFileEntry().getBytes()).isEqualTo(bytes);
//
//        List<FileEntryModel> entries = uncompressedFile.getEntries();
//
//        assertThat(entries.size()).isEqualTo(2);
//        assertThat(entries.get(0)).isNotNull();
//        assertThat(entries.get(0).getFilename()).isEqualTo("FF14-1.xml");
//        assertThat(entries.get(1)).isNotNull();
//        assertThat(entries.get(1).getFilename()).isEqualTo("hello.txt");
//    }

    @Test
    public void test() {
    }

}