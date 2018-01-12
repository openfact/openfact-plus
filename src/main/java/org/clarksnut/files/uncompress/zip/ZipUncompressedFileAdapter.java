package org.clarksnut.files.uncompress.zip;

import org.clarksnut.files.uncompress.FileEntryModel;
import org.clarksnut.files.uncompress.UncompressedFileModel;
import org.clarksnut.files.uncompress.exceptions.NotReadableCompressFileException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUncompressedFileAdapter implements UncompressedFileModel {

    private final String filename;
    private final byte[] bytes;

    private List<FileEntryModel> entries;

    public ZipUncompressedFileAdapter(String filename, byte[] bytes) {
        this.filename = filename;
        this.bytes = bytes;
    }

    @Override
    public FileEntryModel getFileEntry() {
        return new FileEntryModel(filename, bytes);
    }

    @Override
    public boolean isCompressedFile() {
        return true;
    }

    @Override
    public List<FileEntryModel> getEntries() throws NotReadableCompressFileException {
        if (entries == null) {
            entries = new ArrayList<>();

            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
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

                    FileEntryModel fileEntry = new FileEntryModel(entryFileName, bos.toByteArray());
                    entries.add(fileEntry);
                }
            } catch (Exception e) {
                throw new NotReadableCompressFileException("Could not unzip");
            }
        }
        return entries;
    }

}
