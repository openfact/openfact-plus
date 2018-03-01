package org.clarksnut;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.jayway.restassured.RestAssured;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.jayway.awaitility.Awaitility.await;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;

/**
 * @author Heiko Braun
 */
@RunWith(Arquillian.class)
public class OpenshiftIT {

    @RouteURL("${app.name}")
    private URL url;

    @Before
    public void setup() {
        await().atMost(5, TimeUnit.MINUTES).until(() -> {
            try {
                return get(url).getStatusCode() == 200;
            } catch (Exception e) {
                return false;
            }
        });

        RestAssured.baseURI = url + "api/greeting";
    }

    @Test
    public void testServiceInvocation() {
        when()
                .get()
                .then()
                .statusCode(200)
                .body(containsString("Hello, World!"));
    }

    @Test
    public void testServiceInvocationWithParam() {
        given()
                .queryParam("name", "Peter")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body(containsString("Hello, Peter!"));
    }
}