package org.openfact.repositories.user;

import org.jboss.logging.Logger;
import org.openfact.models.BrokerType;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.annotation.Annotation;

@Stateless
public class MailUtils {

    private static final Logger logger = Logger.getLogger(MailUtils.class);

    @Inject
    @Any
    private Instance<MailReader> mailReaders;

    public MailReader getMailReader(BrokerType brokerType){
        Annotation annotation = new MailVendorTypeLiteral(brokerType);
        Instance<MailReader> instance = mailReaders.select(annotation);
        if (instance.isAmbiguous() || instance.isUnsatisfied()) {
            logger.warn("Could not find a reader for:" + brokerType);
            return null;
        }
        return instance.get();
    }

}
