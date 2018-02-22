package org.clarksnut.mapper.document.pe.perception;

import org.clarksnut.documents.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapperProvider;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;
import org.openfact.perception.PerceptionType;

public class PEPerceptionParsedDocumentProvider implements DocumentMapperProvider {

    private static final Logger logger = Logger.getLogger(PEPerceptionParsedDocumentProvider.class);

    @Override
    public String getGroup() {
        return "peru";
    }

    @Override
    public String getSupportedDocumentType() {
        return "Perception";
    }

    @Override
    public DocumentMapped map(XmlUBLFileModel file) throws ImpossibleToUnmarshallException {
        PerceptionType perceptionType;
        try {
            perceptionType = ClarksnutModelUtils.unmarshall(file.getDocument(), PerceptionType.class);
        } catch (Exception e) {
            throw new ImpossibleToUnmarshallException("Could not marshall to:" + PerceptionType.class.getName());
        }

        return new DocumentMapped() {
            @Override
            public DocumentBean getBean() {
                return new PEPerceptionBeanAdapter(perceptionType);
            }

            @Override
            public Object getType() {
                return perceptionType;
            }
        };
    }

}
