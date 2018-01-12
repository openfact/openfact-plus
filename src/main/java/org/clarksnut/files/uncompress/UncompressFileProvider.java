package org.clarksnut.files.uncompress;

public interface UncompressFileProvider {

    String getFileExtensionSupported();

    UncompressedFileModel uncompress(String filename, byte[] bytes);

}
