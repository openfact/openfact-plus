package org.openfact.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class GreetingService {

    @GET
    @Path("/greeting")
    @Produces("application/json")
    public String greeting() {
        return "Hello, World!";
    }

}
