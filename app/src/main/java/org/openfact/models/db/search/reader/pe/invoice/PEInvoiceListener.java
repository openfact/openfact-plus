package org.openfact.models.db.search.reader.pe.invoice;

import org.openfact.models.DocumentModel.DocumentCreationEvent;
import org.openfact.models.DocumentModel.DocumentRemovedEvent;
import org.openfact.models.db.search.reader.SupportedType;

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
