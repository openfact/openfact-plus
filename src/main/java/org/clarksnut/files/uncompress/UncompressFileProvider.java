package org.clarksnut.files.uncompress;

import org.clarksnut.files.FileModel;
import org.clarksnut.files.uncompress.exceptions.NotReadableCompressFileException;

import java.util.List;

public interface UncompressFileProvider {

    String getFileExtensionSupported();

    List<FileModel> uncompress(FileModel file) throws NotReadableCompressFileException;

}
