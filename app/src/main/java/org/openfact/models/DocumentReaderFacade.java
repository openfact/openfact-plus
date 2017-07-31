package org.openfact.models;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.w3c.dom.Document;

public interface DocumentReaderFacade {

    XContentBuilder read(Document document);

}
