package org.openfact.services.resources.camel;

import org.apache.camel.builder.RouteBuilder;

import javax.inject.Singleton;

/**
 * The Hello World example of integration kits, which is moving a file.
 */
@Singleton
public class FileMoveRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:target/inbox")
            .to("file:target/outbox");
    }

}
