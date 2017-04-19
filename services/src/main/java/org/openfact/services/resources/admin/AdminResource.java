package org.openfact.services.resources.admin;

import org.jboss.logging.Logger;
import org.openfact.OpenfactService;
import org.openfact.models.ModelException;
import org.openfact.models.OpenfactProvider;
import org.openfact.syncronization.SyncronizationModel;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.math.BigInteger;

@Path("/admin")
@Stateless
public class AdminResource {

    private final static Logger logger = Logger.getLogger(AdminResource.class);

    @Inject
    private OpenfactService openfactService;

    @Inject
    private OpenfactProvider openfactProvider;

    @POST
    @Path("synchronize")
    @Asynchronous
    public void synchronize() {
        SyncronizationModel syncronizationModel = openfactProvider.getSyncronizationModel();
        BigInteger startHistoryId = syncronizationModel.getHistoryId();
        try {
            openfactService.synchronize(startHistoryId);
        } catch (ModelException e) {
            logger.error("Could not syncronize");
        }
    }

}
