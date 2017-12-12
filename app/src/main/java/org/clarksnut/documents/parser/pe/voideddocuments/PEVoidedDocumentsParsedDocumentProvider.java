package org.clarksnut.documents.parser.pe.voideddocuments;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import org.clarksnut.documents.parser.ParsedDocument;
import org.clarksnut.documents.parser.ParsedDocumentProvider;
import org.clarksnut.documents.parser.SkeletonDocument;
import org.clarksnut.documents.parser.SupportedDocumentType;
import org.clarksnut.documents.parser.pe.PEUtils;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@SupportedDocumentType(value = "VoidedDocuments")
public class PEVoidedDocumentsParsedDocumentProvider implements ParsedDocumentProvider {

    private static final Logger logger = Logger.getLogger(PEVoidedDocumentsParsedDocumentProvider.class);

    @Override
    public String getSupportedDocumentType() {
        return "VoidedDocuments";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public ParsedDocument read(XmlUBLFileModel file) {
        VoidedDocumentsType voidedDocumentsType;
        try {
            voidedDocumentsType = ClarksnutModelUtils.unmarshall(file.getDocument(), VoidedDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }

        SkeletonDocument skeleton = new SkeletonDocument();
        skeleton.setType(getSupportedDocumentType());
        skeleton.setAssignedId(voidedDocumentsType.getID().getValue());
        skeleton.setSupplierAssignedId(voidedDocumentsType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        skeleton.setSupplierName(voidedDocumentsType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        skeleton.setIssueDate(voidedDocumentsType.getIssueDate().getValue().toGregorianCalendar().getTime());

        // Postal address
        AddressType postalAddressType = voidedDocumentsType.getAccountingSupplierParty().getParty().getPostalAddress();
        if (postalAddressType != null) {
            skeleton.setSupplierStreetAddress(postalAddressType.getStreetName().getValue());
            skeleton.setSupplierCountry(postalAddressType.getCountry().getIdentificationCode().getValue());
            skeleton.setSupplierCity(PEUtils.toCityString(postalAddressType));
        }

        return new ParsedDocument() {
            @Override
            public SkeletonDocument getSkeleton() {
                return skeleton;
            }

            @Override
            public Object getType() {
                return voidedDocumentsType;
            }
        };
    }

}
