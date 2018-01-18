package org.clarksnut.services.resources;

import org.wildfly.swarm.health.Health;
import org.wildfly.swarm.health.HealthStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/app")
public class HealthCheckResource {

    @GET
    @Path("/startup")
    @Health
    public HealthStatus checkSomethingElse() {
        return HealthStatus.named("startup");
    }

}
