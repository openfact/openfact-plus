package org.clarksnut.mapper.document.pe.voideddocuments;

import org.clarksnut.documents.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapperProvider;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;

public class PEVoidedDocumentsParsedDocumentProvider implements DocumentMapperProvider {

    private static final Logger logger = Logger.getLogger(PEVoidedDocumentsParsedDocumentProvider.class);

    @Override
    public String getGroup() {
        return "peru";
    }

    @Override
    public String getSupportedDocumentType() {
        return "VoidedDocuments";
    }

    @Override
    public DocumentMapped map(XmlUBLFileModel file) throws ImpossibleToUnmarshallException {
        VoidedDocumentsType voidedDocumentsType;
        try {
            voidedDocumentsType = ClarksnutModelUtils.unmarshall(file.getDocument(), VoidedDocumentsType.class);
        } catch (Exception e) {
            throw new ImpossibleToUnmarshallException("Could not marshall to:" + VoidedDocumentsType.class.getName());
        }

        return new DocumentMapped() {
            @Override
            public DocumentBean getBean() {
                return new PEVoidedDocumentsBeanAdapter(voidedDocumentsType);
            }

            @Override
            public Object getType() {
                return voidedDocumentsType;
            }
        };
    }

}
