package org.clarksnut.files.uncompress;

import org.clarksnut.files.uncompress.exceptions.NotReadableCompressFileException;

import java.util.List;

public interface UncompressedFileModel {

    FileEntryModel getFileEntry();

    boolean isCompressedFile();

    List<FileEntryModel> getEntries() throws NotReadableCompressFileException;

}
