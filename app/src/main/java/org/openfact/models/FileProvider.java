package org.openfact.models;

public interface FileProvider {

    /**
     * @param file      file value
     * @param extension file extension e.g. .pdf
     */
    FileModel addFile(byte[] file, String extension) throws StorageException;

    boolean removeFile(FileModel file) throws StorageException;

}
