package org.clarksnut.files;

public class FlyWeightFileModel implements FileModel {

    protected final FileModel file;

    protected byte[] fileValue;
    protected Long fileChecksum;

    public FlyWeightFileModel(FileModel file) {
        this.file = file;
    }

    @Override
    public String getId() {
        return file.getId();
    }

    @Override
    public String getFilename() {
        return file.getFilename();
    }

    @Override
    public byte[] getFile() {
        if (fileValue == null) {
            fileValue = file.getFile();
        }
        return fileValue;
    }

    @Override
    public long getChecksum() {
        if (fileChecksum == null) {
            fileChecksum = file.getChecksum();
        }
        return fileChecksum;
    }

}
