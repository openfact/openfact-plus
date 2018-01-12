package org.clarksnut.files.uncompress.generic;

import org.clarksnut.files.uncompress.UncompressFileProvider;
import org.clarksnut.files.uncompress.UncompressedFileModel;

public class GenericUncompressFileProvider implements UncompressFileProvider {

    @Override
    public String getFileExtensionSupported() {
        return "*";
    }

    @Override
    public UncompressedFileModel uncompress(String filename, byte[] bytes) {
        return new GenericUncompressedFileAdapter(filename, bytes);
    }

}
