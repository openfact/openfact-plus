package org.openfact.services.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class GreetingService {

    @GET
    @Path("/greeting")
    public String greeting() {
        return "Hello, World!";
    }

}
