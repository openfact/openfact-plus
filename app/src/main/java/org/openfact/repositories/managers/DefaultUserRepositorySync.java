package org.openfact.repositories.managers;

import org.jboss.logging.Logger;
import org.openfact.models.ParseExceptionModel;
import org.openfact.models.StorageException;
import org.openfact.models.UserRepositoryModel;
import org.openfact.repositories.user.*;
import org.openfact.repositories.user.MailVendorTypeLiteral;
import org.openfact.services.managers.DocumentManager;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.time.LocalDateTime;

@Stateless
public class DefaultUserRepositorySync implements MailSynchronizer {

    private static final Logger logger = Logger.getLogger(DefaultUserRepositorySync.class);

//    @Inject
//    @Any
//    private Instance<MailReader> readers;
//
//    @Inject
//    private DocumentManager documentManager;

    public void synchronize(UserRepositoryModel repository) {
//        try {
//            // Obtain readers
//            MailReader reader = getReader(repository);
//            if (reader == null) {
//                logger.error("Could not find a valid readers for " + repository.getType());
//                return;
//            }
//
//            // Build query
//            LocalDateTime currentTime = LocalDateTime.now();
//            LocalDateTime lastTimeSynchronized = repository.getLastTimeSynchronized();
//
//            MailQuery.Builder queryBuilder = MailQuery.builder();
//            queryBuilder.to(currentTime);
//            if (lastTimeSynchronized != null) {
//                queryBuilder.from(lastTimeSynchronized);
//            }
//
//            MailQuery query = queryBuilder.build();
//
//            // Get repository elements
//            for (MailUBLMessage element : reader.read(repository, query)) {
//                byte[] bytes = element.getXml();
//                try {
//                    documentManager.importDocument(bytes);
//                } catch (ParseExceptionModel | StorageException e) {
//                    logger.error("Error importing element repository...", e);
//                }
//            }
//
//            // Update repository
//            repository.setLastTimeSynchronized(currentTime);
//        } catch (MailReadException e) {
//            logger.error("Could not read repository...", e);
//        }
    }

//    private MailReader getReader(UserRepositoryModel repository) {
//        Annotation annotation = new MailVendorTypeLiteral(repository.getType());
//        Instance<MailReader> instance = readers.select(annotation);
//        if (!instance.isAmbiguous() && !instance.isUnsatisfied()) {
//            return instance.get();
//        }
//        return null;
//    }

}
