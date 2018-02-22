package org.clarksnut.mapper.document.pe.summarydocuments;

import org.clarksnut.documents.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapperProvider;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;

public class PESummaryDocumentsParsedDocumentProvider implements DocumentMapperProvider {

    private static final Logger logger = Logger.getLogger(PESummaryDocumentsParsedDocumentProvider.class);

    @Override
    public String getGroup() {
        return "peru";
    }

    @Override
    public String getSupportedDocumentType() {
        return "SummaryDocuments";
    }

    @Override
    public DocumentMapped map(XmlUBLFileModel file) throws ImpossibleToUnmarshallException {
        SummaryDocumentsType summaryDocumentsType;
        try {
            summaryDocumentsType = ClarksnutModelUtils.unmarshall(file.getDocument(), SummaryDocumentsType.class);
        } catch (Exception e) {
            throw new ImpossibleToUnmarshallException("Could not marshall to:" + SummaryDocumentsType.class.getName());
        }

        return new DocumentMapped() {
            @Override
            public DocumentBean getBean() {
                return new PESummaryDocumentsBeanAdapter(summaryDocumentsType);
            }

            @Override
            public Object getType() {
                return summaryDocumentsType;
            }
        };
    }

}
