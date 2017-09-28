package org.openfact.models.db.es.reader.pe.invoice;

import org.openfact.models.DocumentModel.DocumentCreationEvent;
import org.openfact.models.DocumentModel.DocumentRemovedEvent;
import org.openfact.models.db.es.reader.LocationType;
import org.openfact.models.db.es.reader.MapperType;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Stateless
public class PEInvoiceListener {

    @Inject
    private EntityManager em;

    public void creationListener(@Observes() @MapperType(value = "Invoice") @LocationType(value = "peru") DocumentCreationEvent createdDocument) {

    }

    public void removeListener(@Observes() @MapperType(value = "Invoice") @LocationType(value = "peru") DocumentRemovedEvent removedDocument) {

    }

}
