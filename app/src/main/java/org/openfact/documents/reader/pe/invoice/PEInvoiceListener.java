package org.openfact.documents.reader.pe.invoice;

import org.openfact.documents.DocumentModel.DocumentCreationEvent;
import org.openfact.documents.DocumentModel.DocumentRemovedEvent;
import org.openfact.documents.reader.SupportedType;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Stateless
public class PEInvoiceListener {

    @Inject
    private EntityManager em;

    public void creationListener(@Observes() @SupportedType(value = "Invoice") DocumentCreationEvent createdDocument) {

    }

    public void removeListener(@Observes() @SupportedType(value = "Invoice") DocumentRemovedEvent removedDocument) {

    }

}
