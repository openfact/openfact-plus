package org.clarksnut.files;

public interface FileProvider {

    /**
     * @param file file value
     */
    FileModel addFile(String filename, byte[] file);

    /**
     * @param id file unique identity
     * @return file model
     */
    FileModel getFile(String id);

    /**
     * @param file to be removed
     * @return result of operation
     */
    boolean removeFile(FileModel file);

}
