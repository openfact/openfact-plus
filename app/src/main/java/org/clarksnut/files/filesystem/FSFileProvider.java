package org.clarksnut.files.filesystem;

import org.clarksnut.files.FileModel;
import org.clarksnut.files.FileProvider;
import org.clarksnut.files.exceptions.FileStorageException;
import org.clarksnut.models.utils.ClarksnutModelUtils;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Stateless
@Alternative
@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
public class FSFileProvider implements FileProvider {

    private String FILESYSTEM_CLUSTER_PATH;

    @PostConstruct
    private void init() {
        String fileSystemPath = System.getenv("FILESYSTEM_CLUSTER_PATH");
        if (fileSystemPath != null && !fileSystemPath.trim().isEmpty()) {
            FILESYSTEM_CLUSTER_PATH = fileSystemPath;
        } else {
            FILESYSTEM_CLUSTER_PATH = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        }
    }

    @Override
    public FileModel addFile(byte[] file, String extension) throws FileStorageException {
        Path path = getBasePath().resolve(ClarksnutModelUtils.generateId() + extension);
        try {
            path = Files.write(path, file);
        } catch (IOException e) {
            throw new FileStorageException("Could not write file:" + path.toAbsolutePath().toString(), e);
        }

        return new FileAdapter(path);
    }

    @Override
    public FileModel getFile(String id) {
        Path path = Paths.get(FILESYSTEM_CLUSTER_PATH).resolve(id);
        if (!Files.exists(path)) return null;
        return new FileAdapter(path);
    }

    @Override
    public boolean removeFile(FileModel file) throws FileStorageException {
        Path path = Paths.get(file.getId());
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new FileStorageException("Could not delete file:" + path.toAbsolutePath().toString(), e);
        }
        return true;
    }

    private Path getBasePath() {
        return Paths.get(FILESYSTEM_CLUSTER_PATH);
    }

}
