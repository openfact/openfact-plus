package org.clarksnut.documents;

import org.clarksnut.documents.jpa.JpaDocumentProvider;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@Startup
public class DocumentIndexer {

    private static final Logger logger = Logger.getLogger(JpaDocumentProvider.class);

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    private void init() {
//        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
//        try {
//            fullTextEntityManager.createIndexer().startAndWait();
//        } catch (InterruptedException e) {
//            logger.error("Error indexing documents", e);
//        }
    }

}
