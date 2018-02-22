package org.clarksnut.files;

public class BasicFileModel implements FileModel {

    private final String filename;
    private final byte[] file;

    public BasicFileModel(String filename, byte[] file) {
        this.filename = filename;
        this.file = file;
    }

    @Override
    public String getId() {
        return null;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public byte[] getFile() {
        return file;
    }

    @Override
    public long getChecksum() {
        return FileModelUtils.getChecksum(getFile());
    }

}
