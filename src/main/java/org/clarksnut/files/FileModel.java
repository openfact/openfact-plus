package org.clarksnut.files;

public interface FileModel {

    String getId();

    String getFilename();

    byte[] getFile();

    long getChecksum();
}
