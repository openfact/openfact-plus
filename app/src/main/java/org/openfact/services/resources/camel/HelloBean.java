package org.openfact.services.resources.camel;

import org.apache.camel.PropertyInject;
import org.apache.camel.util.InetAddressUtil;

import javax.inject.Singleton;

/**
 * Define this bean as singleton so CDI can use it for dependency injection
 */
@Singleton
public class HelloBean {

    // use @PropertyInject("reply") to inject the property placeholder with the key: reply
    @PropertyInject("reply")
    private String msg;

    public String sayHello() throws Exception {
        return msg + " from " + InetAddressUtil.getLocalHostName();
    }

}
