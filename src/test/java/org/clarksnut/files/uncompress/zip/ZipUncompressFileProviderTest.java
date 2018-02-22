package org.clarksnut.files.uncompress.zip;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

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
//        assertThat(uncompressedFile.getFileEntry().getFile()).isEqualTo(filename);
//        assertThat(uncompressedFile.getFileEntry().getBytes()).isEqualTo(bytes);
//
//        List<FileEntryModel> entries = uncompressedFile.getEntries();
//
//        assertThat(entries.size()).isEqualTo(2);
//        assertThat(entries.get(0)).isNotNull();
//        assertThat(entries.get(0).getFile()).isEqualTo("FF14-1.xml");
//        assertThat(entries.get(1)).isNotNull();
//        assertThat(entries.get(1).getFile()).isEqualTo("hello.txt");
//    }

    @Test
    public void test() {
    }

}