package org.openfact.services.resources.camel;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ESRouteBuilder extends RouteBuilder {

    //@Override
    public void configure() throws Exception {
        from("direct:index")
                .to("elasticsearch://local?operation=INDEX&indexName=twitter&indexType=tweet");
    }

}
