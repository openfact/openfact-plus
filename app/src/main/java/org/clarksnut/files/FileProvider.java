package org.clarksnut.files;

import org.clarksnut.files.exceptions.FileStorageException;

public interface FileProvider {

    /**
     * @param file      file value
     * @param extension file extension e.g. .pdf
     */
    FileModel addFile(byte[] file, String extension) throws FileStorageException;

    /**
     * @param id file unique identity
     * @return file model
     */
    FileModel getFile(String id);

    /**
     * @param file to be removed
     * @return result of operation
     */
    boolean removeFile(FileModel file) throws FileStorageException;

}
