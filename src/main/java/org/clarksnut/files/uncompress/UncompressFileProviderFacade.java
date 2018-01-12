package org.clarksnut.files.uncompress;

import org.apache.commons.io.FilenameUtils;

public class UncompressFileProviderFacade {

    public UncompressedFileModel uncompress(String filename, byte[] bytes) {
        String fileExtension = FilenameUtils.getExtension(filename);
        UncompressFileProvider provider = UncompressFileProviderFactory
                .getInstance()
                .getUncompressFileProvider(fileExtension);

        return provider.uncompress(filename, bytes);
    }

}
