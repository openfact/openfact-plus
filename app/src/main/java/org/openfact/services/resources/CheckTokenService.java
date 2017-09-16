package org.openfact.services.resources;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("/check_token")
@Consumes(MediaType.APPLICATION_JSON)
public class CheckTokenService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser() {
        return Response.ok().build();
    }

}