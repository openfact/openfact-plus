package org.clarksnut.files.uncompress.generic;

import org.clarksnut.files.uncompress.FileEntryModel;
import org.clarksnut.files.uncompress.UncompressedFileModel;

import java.util.Collections;
import java.util.List;

public class GenericUncompressedFileAdapter implements UncompressedFileModel {

    private final String filename;
    private final byte[] bytes;

    public GenericUncompressedFileAdapter(String filename, byte[] bytes) {
        this.filename = filename;
        this.bytes = bytes;
    }

    @Override
    public FileEntryModel getFileEntry() {
        return new FileEntryModel(filename, bytes);
    }

    @Override
    public boolean isCompressedFile() {
        return false;
    }

    @Override
    public List<FileEntryModel> getEntries() {
        return Collections.emptyList();
    }

}
