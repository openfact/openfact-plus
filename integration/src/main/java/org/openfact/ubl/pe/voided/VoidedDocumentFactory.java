package org.openfact.ubl.pe.voided;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * Created by lxpary on 27/12/16.
 */
@XmlRegistry
public class VoidedDocumentFactory {
    private final static QName _VoidedDocuments_QNAME = new QName(
            "urn:sunat:names:specification:ubl:peru:schema:xsd:VoidedDocuments-1", "VoidedDocuments");

    public VoidedDocumentFactory() {
    }

    @XmlElementDecl(namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:VoidedDocuments-1", name = "VoidedDocuments")
    public JAXBElement<VoidedDocumentsType> createVoidedDocuments(VoidedDocumentsType value) {
        return new JAXBElement<VoidedDocumentsType>(_VoidedDocuments_QNAME, VoidedDocumentsType.class, null,
                value);
    }
}
