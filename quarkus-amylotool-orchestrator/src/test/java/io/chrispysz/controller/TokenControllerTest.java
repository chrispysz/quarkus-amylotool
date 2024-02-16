package io.chrispysz.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@WithDBData(value = {"data/tokens.xml"}, deleteBeforeInsert = true, rinseAndRepeat = true)
class TokenControllerTest {

    private static final String NEW_TOKEN_VALUE = "45338a22-b747-4470-b6a4-36adcacf00c4";
    private static final String OLD_TOKEN_VALUE = "93082a72-2244-4bca-93e0-e9ae49a9822f";

    @Test
    void shouldFindTokenWhenValueExists() {
        given()
                .pathParam("value", NEW_TOKEN_VALUE)
                .when()
                .get("api/token/{value}")
                .then()
                .statusCode(OK.getStatusCode())
                .body("name", is("test_token_new"))
                .body("value", is(NEW_TOKEN_VALUE))
                .body("validUntil", is("2051-06-13T17:09:42.411"));

    }

    @Test
    void shouldNotFindTokenWhenValueNotExists() {
        var wrongValue = "wrong!value!";

        given()
                .pathParam("value", wrongValue)
                .when()
                .get("api/token/{value}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }

}