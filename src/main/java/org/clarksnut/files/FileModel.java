package org.clarksnut.files;

public interface FileModel {

    String getId();

    String getFilename();

    /**
     * @return file value on bytes
     */
    byte[] getFileAsBytes() ;
}
