package org.openfact.services.resources.camel;

import org.apache.camel.builder.RouteBuilder;

public class CopyRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:data/inbox?noop=true")
                .to("file:data/outboox");
    }

}
