package org.openfact.documents.reader.pe.voideddocuments;

import org.jboss.logging.Logger;
import org.openfact.documents.DocumentReader;
import org.openfact.documents.GenericDocument;
import org.openfact.documents.jpa.entity.DocumentEntity;
import org.openfact.documents.reader.SupportedType;
import org.openfact.files.XmlUBLFileModel;
import org.openfact.models.utils.OpenfactModelUtils;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;

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
            voidedDocumentsType = OpenfactModelUtils.unmarshall(file.getDocument(), VoidedDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setAssignedId(voidedDocumentsType.getID().getValue());
        documentEntity.setSupplierAssignedId(voidedDocumentsType.getAccountingSupplierParty().getCustomerAssignedAccountID().getValue());
        documentEntity.setSupplierName(voidedDocumentsType.getAccountingSupplierParty().getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        documentEntity.setIssueDate(voidedDocumentsType.getIssueDate().getValue().toGregorianCalendar().getTime());

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
