package org.openfact.ubl.pe.summary;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * Created by lxpary on 27/12/16.
 */
@XmlRegistry
public class SummaryDocumentFactory {
    private final static QName _SummaryDocuments_QNAME = new QName(
            "urn:sunat:names:specification:ubl:peru:schema:xsd:SummaryDocuments-1", "SummaryDocuments");

    public SummaryDocumentFactory() {
    }

    @XmlElementDecl(namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SummaryDocuments-1", name = "SummaryDocuments")
    public JAXBElement<SummaryDocumentsType> createSummaryDocuments(SummaryDocumentsType value) {
        return new JAXBElement<SummaryDocumentsType>(_SummaryDocuments_QNAME, SummaryDocumentsType.class,
                null, value);
    }
}
