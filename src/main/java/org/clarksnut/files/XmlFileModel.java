package org.clarksnut.files;

import org.w3c.dom.Document;

public interface XmlFileModel extends FileModel {

    Document getDocument() throws Exception;

}
