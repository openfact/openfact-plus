package org.clarksnut.mapper.document.pe.retention;

import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.mapper.document.DocumentMapped;
import org.clarksnut.mapper.document.DocumentMapperProvider;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;
import org.openfact.retention.RetentionType;

import javax.xml.bind.JAXBException;

public class PERetentionParsedDocumentProvider implements DocumentMapperProvider {

    private static final Logger logger = Logger.getLogger(PERetentionParsedDocumentProvider.class);

    @Override
    public String getGroup() {
        return "peru";
    }

    @Override
    public String getSupportedDocumentType() {
        return "Retention";
    }

    @Override
    public DocumentMapped map(XmlUBLFileModel file) {
        RetentionType retentionType;
        try {
            retentionType = ClarksnutModelUtils.unmarshall(file.getDocument(), RetentionType.class);
        } catch (JAXBException e) {
            return null;
        }

        return new DocumentMapped() {
            @Override
            public DocumentBean getBean() {
                return new PERetentionBeanAdapter(retentionType);
            }

            @Override
            public Object getType() {
                return retentionType;
            }
        };
    }

}
