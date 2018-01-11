package org.clarksnut.files;

import org.clarksnut.files.exceptions.FileFetchException;

public class FlyWeightFileModel implements FileModel {

    protected final FileModel fileModel;
    protected byte[] bytes;

    public FlyWeightFileModel(FileModel fileModel) {
        this.fileModel = fileModel;
    }

    @Override
    public String getId() {
        return this.fileModel.getId();
    }

    @Override
    public byte[] getFile() throws FileFetchException {
        if (bytes == null) {
            this.bytes = this.fileModel.getFile();
        }
        return this.bytes;
    }
}
