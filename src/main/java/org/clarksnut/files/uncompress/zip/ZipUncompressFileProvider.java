package org.clarksnut.files.uncompress.zip;

import org.clarksnut.files.BasicFileModel;
import org.clarksnut.files.FileModel;
import org.clarksnut.files.uncompress.UncompressFileProvider;
import org.clarksnut.files.uncompress.exceptions.NotReadableCompressFileException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUncompressFileProvider implements UncompressFileProvider {

    @Override
    public String getFileExtensionSupported() {
        return "zip";
    }

    @Override
    public List<FileModel> uncompress(FileModel file) throws NotReadableCompressFileException {
        List<FileModel> entries = new ArrayList<>();

        ByteArrayInputStream bis = new ByteArrayInputStream(file.getFile());
        try (ZipInputStream zis = new ZipInputStream(bis)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryFileName = entry.getName();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                byte buffer[] = new byte[1024];
                int count;
                while ((count = zis.read(buffer)) > 0) {
                    bos.write(buffer, 0, count);
                }

                FileModel fileEntry = new BasicFileModel(entryFileName, bos.toByteArray());
                entries.add(fileEntry);
            }
        } catch (Exception e) {
            throw new NotReadableCompressFileException("Could not unzip");
        }

        return entries;
    }

}
