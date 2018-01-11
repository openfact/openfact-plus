package org.clarksnut.files;

import org.clarksnut.files.exceptions.FileFetchException;

public interface FileModel {

    String getId();

    /**
     * @return file value on bytes
     */
    byte[] getFile() throws FileFetchException;
}
