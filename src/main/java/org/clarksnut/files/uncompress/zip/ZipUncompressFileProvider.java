package org.clarksnut.files.uncompress.zip;

import org.clarksnut.files.uncompress.UncompressFileProvider;
import org.clarksnut.files.uncompress.UncompressedFileModel;

public class ZipUncompressFileProvider implements UncompressFileProvider {

    @Override
    public String getFileExtensionSupported() {
        return "zip";
    }

    @Override
    public UncompressedFileModel uncompress(String filename, byte[] bytes) {
        return new ZipUncompressedFileAdapter(filename, bytes);
    }

}
