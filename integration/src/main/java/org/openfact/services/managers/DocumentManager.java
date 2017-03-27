package org.openfact.services.managers;

import org.jboss.logging.Logger;
import org.openfact.models.DocumentProvider;
import org.openfact.models.utils.UBLUtil;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class DocumentManager {

    protected static final Logger logger = Logger.getLogger(DocumentManager.class);

    @Inject
    protected DocumentProvider model;

    @Inject
    private DocumentProvider documentProvider;

    @Inject
    private UBLUtil ublUtil;

}
