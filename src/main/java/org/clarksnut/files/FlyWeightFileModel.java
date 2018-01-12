package org.clarksnut.files;

public class FlyWeightFileModel implements FileModel {

    protected final FileModel model;
    protected byte[] bytes;

    public FlyWeightFileModel(FileModel fileModel) {
        this.model = fileModel;
    }

    @Override
    public String getId() {
        return this.model.getId();
    }

    @Override
    public String getFilename() {
        return model.getFilename();
    }

    @Override
    public byte[] getFile() {
        if (bytes == null) {
            this.bytes = this.model.getFile();
        }
        return this.bytes;
    }
}
