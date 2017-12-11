package org.clarksnut.documents.parser.pe.summarydocuments;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import org.clarksnut.documents.parser.ParsedDocument;
import org.clarksnut.documents.parser.ParsedDocumentProvider;
import org.clarksnut.documents.parser.SkeletonDocument;
import org.clarksnut.documents.parser.SupportedDocumentType;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@SupportedDocumentType(value = "SummaryDocuments")
public class PESummaryDocumentsReader implements ParsedDocumentProvider {

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
    public ParsedDocument read(XmlUBLFileModel file) {
        SummaryDocumentsType summaryDocumentsType;
        try {
            summaryDocumentsType = ClarksnutModelUtils.unmarshall(file.getDocument(), SummaryDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }

        SkeletonDocument skeleton = new SkeletonDocument();
        skeleton.setAssignedId(summaryDocumentsType.getID().getValue());
        skeleton.setSupplierAssignedId(summaryDocumentsType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        skeleton.setSupplierName(summaryDocumentsType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setIssueDate(summaryDocumentsType.getIssueDate().getValue().toGregorianCalendar().getTime());

        // Postal address
        AddressType postalAddressType = summaryDocumentsType.getAccountingSupplierParty().getParty().getPostalAddress();
        skeleton.setSupplierStreetAddress(postalAddressType.getStreetName().getValue());
        skeleton.setSupplierCity(postalAddressType.getCitySubdivisionName().getValue() + ", " + postalAddressType.getCityName().getValue() + ", " + postalAddressType.getCitySubdivisionName().getValue());
        skeleton.setSupplierCountry(postalAddressType.getCountry().getIdentificationCode().getValue());

        return new ParsedDocument() {
            @Override
            public SkeletonDocument getSkeleton() {
                return skeleton;
            }

            @Override
            public Object getType() {
                return summaryDocumentsType;
            }
        };
    }

}
