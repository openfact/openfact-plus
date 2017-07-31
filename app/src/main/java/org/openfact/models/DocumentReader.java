package org.openfact.models;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.w3c.dom.Document;

public interface DocumentReader {

    boolean canRead(String documentType);

    XContentBuilder read(Document document);

}
