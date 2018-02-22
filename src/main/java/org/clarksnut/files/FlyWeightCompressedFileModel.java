package org.clarksnut.files;

import org.clarksnut.files.uncompress.exceptions.NotReadableCompressFileException;

import java.util.List;

public class FlyWeightCompressedFileModel implements CompressedFileModel {

    private final CompressedFileModel file;
    private List<FileModel> children;

    public FlyWeightCompressedFileModel(CompressedFileModel file) {
        this.file = file;
    }

    @Override
    public List<FileModel> getChildren() throws NotReadableCompressFileException {
        if (children == null) {
            children = FileModelUtils.uncompress(file);
        }
        return children;
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
        return file.getFile();
    }

    @Override
    public long getChecksum() {
        return file.getChecksum();
    }
}
