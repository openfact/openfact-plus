package org.clarksnut.files;

import org.apache.commons.io.FilenameUtils;
import org.clarksnut.files.uncompress.UncompressFileProvider;
import org.clarksnut.files.uncompress.UncompressFileProviderFactory;
import org.clarksnut.files.uncompress.exceptions.NotReadableCompressFileException;

import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class FileModelUtils {

    public static long getChecksum(byte[] bytes) {
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }

    public static boolean isXmlOrCompressedFilename(String filename) {
        String fileExtension = FilenameUtils.getExtension(filename);
        return fileExtension.equalsIgnoreCase("xml") || isCompressedFilename(filename);
    }

    public static boolean isCompressedFilename(String filename) {
        String fileExtension = FilenameUtils.getExtension(filename);
        if (fileExtension.equalsIgnoreCase("zip") ||
                fileExtension.equalsIgnoreCase("tar.gz") ||
                fileExtension.equalsIgnoreCase("rar")) {
            return true;
        }
        return false;
    }

    public static boolean isXmlFile(FileModel file) {
        return FilenameUtils.getExtension(file.getFilename()).equalsIgnoreCase("xml");
    }

    public static boolean isCompressFile(FileModel file) {
        String fileExtension = FilenameUtils.getExtension(file.getFilename());
        if (fileExtension.equalsIgnoreCase("zip") ||
                fileExtension.equalsIgnoreCase("tar.gz") ||
                fileExtension.equalsIgnoreCase("rar")) {
            return true;
        }
        return false;
    }

    public static List<FileModel> uncompress(FileModel file) throws NotReadableCompressFileException {
        String fileExtension = FilenameUtils.getExtension(file.getFilename());
        UncompressFileProvider provider = UncompressFileProviderFactory
                .getInstance()
                .getUncompressFileProvider(fileExtension);
        if (provider == null) {
            throw new NotReadableCompressFileException("No provider for compressed file type found");
        }

        return provider.uncompress(file);
    }
}
