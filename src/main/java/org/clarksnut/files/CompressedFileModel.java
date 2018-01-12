package org.clarksnut.files;

import java.util.List;

public interface CompressedFileModel extends FileModel {

    FileModel getModel();

    boolean isCompressed();

    List<FileModel> getChildrenIfExists();

}
