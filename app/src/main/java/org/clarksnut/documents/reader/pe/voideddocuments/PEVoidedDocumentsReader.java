package org.clarksnut.documents.reader.pe.voideddocuments;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import org.clarksnut.documents.DocumentReader;
import org.clarksnut.documents.GenericDocument;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.jboss.logging.Logger;
import org.clarksnut.documents.reader.SupportedType;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@SupportedType(value = "VoidedDocuments")
public class PEVoidedDocumentsReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PEVoidedDocumentsReader.class);

    @Override
    public String getSupportedDocumentType() {
        return "VoidedDocuments";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        VoidedDocumentsType voidedDocumentsType;
        try {
            voidedDocumentsType = ClarksnutModelUtils.unmarshall(file.getDocument(), VoidedDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setAssignedId(voidedDocumentsType.getID().getValue());
        documentEntity.setSupplierAssignedId(voidedDocumentsType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setSupplierName(voidedDocumentsType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setIssueDate(voidedDocumentsType.getIssueDate().getValue().toGregorianCalendar().getTime());

        // Postal address
        AddressType postalAddressType = voidedDocumentsType.getAccountingSupplierParty().getParty().getPostalAddress();
        documentEntity.setSupplierStreetAddress(postalAddressType.getStreetName().getValue());
        documentEntity.setSupplierCity(postalAddressType.getCitySubdivisionName().getValue() + ", " + postalAddressType.getCityName().getValue() + ", " + postalAddressType.getCitySubdivisionName().getValue());
        documentEntity.setSupplierCountry(postalAddressType.getCountry().getIdentificationCode().getValue());

        return new GenericDocument() {
            @Override
            public DocumentEntity getEntity() {
                return documentEntity;
            }

            @Override
            public Object getJaxb() {
                return voidedDocumentsType;
            }
        };
    }

}
