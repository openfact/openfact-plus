package org.openfact.models.utils;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.jboss.logging.Logger;
import org.openfact.models.DocumentReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Stateless
public class DocumentUtil {

    public static final Logger logger = Logger.getLogger(DocumentUtil.class);

    @Inject
    @Any
    private Instance<DocumentReader> readers;

    public XContentBuilder read(Document document) {
        String documentType = getDocumentType(document);
        for (DocumentReader reader : readers) {
            if (reader.canRead(documentType)) {
                return reader.read(document);
            }
        }

        logger.warn("Could not read xml document[" + documentType + "]");
        return null;
    }

    private String getDocumentType(Document document) {
        Element documentElement = document.getDocumentElement();
        return documentElement.getTagName();
    }

}
