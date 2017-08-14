package org.openfact.models.db.filesystem;

import org.apache.commons.io.FilenameUtils;
import org.openfact.models.FileModel;
import org.openfact.models.StorageException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileAdapter implements FileModel {

    private Path path;

    public FileAdapter(Path path) {
        this.path = path;
    }

    @Override
    public String getId() {
        return path.toAbsolutePath().toString();
    }

    @Override
    public String getFilename() {
        return path.getFileName().toString();
    }

    @Override
    public String getExtension() {
        return FilenameUtils.getExtension(path.getFileName().toString());
    }

    @Override
    public byte[] getFile() throws StorageException {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new StorageException("Could not buildEntity file: " + path.toAbsolutePath().toString(), e);
        }
    }
}
