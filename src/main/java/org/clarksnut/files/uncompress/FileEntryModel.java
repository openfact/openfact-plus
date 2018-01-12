package org.clarksnut.files.uncompress;

public class FileEntryModel {

    private final String filename;
    private final byte[] bytes;

    public FileEntryModel(String filename, byte[] bytes) {
        this.filename = filename;
        this.bytes = bytes;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getBytes() {
        return bytes;
    }

}
