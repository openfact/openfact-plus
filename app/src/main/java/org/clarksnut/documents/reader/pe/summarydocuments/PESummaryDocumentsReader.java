package org.clarksnut.documents.reader.pe.summarydocuments;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import org.clarksnut.documents.DocumentReader;
import org.clarksnut.documents.GenericDocument;
import org.clarksnut.documents.jpa.entity.DocumentEntity;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;
import org.clarksnut.documents.reader.SupportedType;
import org.clarksnut.files.XmlUBLFileModel;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@SupportedType(value = "SummaryDocuments")
public class PESummaryDocumentsReader implements DocumentReader {

    private static final Logger logger = Logger.getLogger(PESummaryDocumentsReader.class);

    @Override
    public String getSupportedDocumentType() {
        return "SummaryDocuments";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public GenericDocument read(XmlUBLFileModel file) {
        SummaryDocumentsType summaryDocumentsType;
        try {
            summaryDocumentsType = ClarksnutModelUtils.unmarshall(file.getDocument(), SummaryDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setAssignedId(summaryDocumentsType.getID().getValue());
        documentEntity.setSupplierAssignedId(summaryDocumentsType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setSupplierName(summaryDocumentsType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setIssueDate(summaryDocumentsType.getIssueDate().getValue().toGregorianCalendar().getTime());

        // Postal address
        AddressType postalAddressType = summaryDocumentsType.getAccountingSupplierParty().getParty().getPostalAddress();
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
                return summaryDocumentsType;
            }
        };
    }

}
