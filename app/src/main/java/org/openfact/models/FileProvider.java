package org.openfact.models;

public interface FileProvider {

    /**
     * @param file      file value
     * @param extension file extension e.g. .pdf
     */
    FileModel addFile(byte[] file, String extension) throws StorageException;

    /**
     * @param file to be removed
     * @return result of operation
     */
    boolean removeFile(FileModel file) throws StorageException;

}
