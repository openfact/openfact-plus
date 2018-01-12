package org.clarksnut.files;

import java.util.List;

public class BasicCompressedFileModel implements CompressedFileModel {

    private final FileModel model;
    private final List<FileModel> children;

    public BasicCompressedFileModel(FileModel model) {
        this.model = model;
        this.children = null;
    }

    public BasicCompressedFileModel(FileModel model, List<FileModel> children) {
        this.model = model;
        this.children = children;
    }

    @Override
    public String getId() {
        return model.getId();
    }

    @Override
    public String getFilename() {
        return model.getFilename();
    }

    @Override
    public byte[] getFile() {
        return model.getFile();
    }

    @Override
    public FileModel getModel() {
        return model;
    }

    @Override
    public boolean isCompressed() {
        return children != null;
    }

    @Override
    public List<FileModel> getChildrenIfExists() {
        return children;
    }
}
