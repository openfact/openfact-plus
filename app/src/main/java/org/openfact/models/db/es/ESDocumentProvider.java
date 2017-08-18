package org.openfact.models.db.es;

import org.jboss.logging.Logger;
import org.openfact.models.DocumentModel;
import org.openfact.models.DocumentProvider;
import org.openfact.models.FileModel;
import org.openfact.models.ModelException;
import org.openfact.models.db.es.entity.DocumentEntity;
import org.openfact.models.db.es.mapper.MapperTypeLiteral;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.lang.annotation.Annotation;

@Stateless
public class ESDocumentProvider implements DocumentProvider {

    private static final Logger logger = Logger.getLogger(ESDocumentProvider.class);

    @Inject
    private EntityManager em;

    @Inject
    @Any
    private Instance<DocumentMapper> mappers;

    @Override
    public DocumentModel addDocument(FileModel file) {
        String documentType;
        try {
            documentType = OpenfactModelUtils.getDocumentType(file.getFile());
        } catch (Exception e) {
            throw new ModelException("Could not read file bytes");
        }

        Annotation annotation = new MapperTypeLiteral(documentType);
        Instance<DocumentMapper> instance = mappers.select(annotation);
        if (instance.isAmbiguous() || instance.isUnsatisfied()) {
            logger.error("Could not find a reader for:" + documentType);
            throw new ModelException("Document[" + documentType + "] not supported");
        }

        DocumentMapper reader = instance.get();
        DocumentEntity entity = reader.buildEntity(file);
        em.persist(entity);
        return new DocumentAdapter(em, entity);
    }

}
