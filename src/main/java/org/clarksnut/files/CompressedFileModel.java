package org.clarksnut.files;

import org.clarksnut.files.uncompress.exceptions.NotReadableCompressFileException;

import java.util.List;

public interface CompressedFileModel extends FileModel {

    List<FileModel> getChildren() throws NotReadableCompressFileException;

}
