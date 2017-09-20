package org.openfact.models.db.es;

import org.jboss.logging.Logger;
import org.openfact.models.UBLDocumentModel;
import org.openfact.models.UBLDocumentProvider;
import org.openfact.models.FileModel;
import org.openfact.models.ModelException;
import org.openfact.models.db.es.entity.UBLDocumentEntity;
import org.openfact.models.db.es.mapper.MapperTypeLiteral;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.lang.annotation.Annotation;

@Stateless
public class ESUBLDocumentProvider implements UBLDocumentProvider {

    private static final Logger logger = Logger.getLogger(ESUBLDocumentProvider.class);

    @Inject
    private EntityManager em;

    @Inject
    @Any
    private Instance<UBLDocumentMapper> mappers;

    @Override
    public UBLDocumentModel addDocument(FileModel file) {
        String documentType;
        try {
            documentType = OpenfactModelUtils.getDocumentType(file.getFile());
        } catch (Exception e) {
            throw new ModelException("Could not read file bytes");
        }

        Annotation annotation = new MapperTypeLiteral(documentType);
        Instance<UBLDocumentMapper> instance = mappers.select(annotation);
        if (instance.isAmbiguous() || instance.isUnsatisfied()) {
            logger.error("Could not find a reader for:" + documentType);
            throw new ModelException("Document[" + documentType + "] not supported");
        }

        UBLDocumentEntity entity = instance.get().map(file);
        entity.setId(OpenfactModelUtils.generateId());
        em.persist(entity);
        
        return new UBLDocumentAdapter(em, entity);
    }

}
