package api.http.facade;

import static io.restassured.RestAssured.given;

import api.configs.AppConfig;
import common.helpers.StepLogger;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.List;

public class HTTPRequestImp<Rec, Res> extends AbstractHTTPRequest implements HTTPRequest<Rec, Res> {
    private final static String API_VERSION = AppConfig.getProperty("apiVersion");

    public HTTPRequestImp(
            RequestSpecification requestSpecification,
            Endpoint endpoint,
            ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public Res post(Rec model) {
        return StepLogger.log("POST request to " + endpoint.getURL(), () -> given()
                .spec(requestSpecification)
                .body(model)
                .post(API_VERSION + endpoint.getURL())
                .then()
                .assertThat()
                .spec(responseSpecification)
                .extract()
                .body()
                .as((Class<Res>) endpoint.getResponseModel()));
    }

    @Override
    public Res get() {
        return StepLogger.log("GET request to " + endpoint.getURL(), () -> given()
                .spec(requestSpecification)
                .get(API_VERSION + endpoint.getURL())
                .then()
                .assertThat()
                .spec(responseSpecification)
                .extract()
                .body()
                .as((Class<Res>) endpoint.getResponseModel()));
    }

    @Override
    public Res put(Rec model) {
        return StepLogger.log("PUT request to " + endpoint.getURL(), () ->
                given()
                        .spec(requestSpecification)
                        .body(model)
                        .put(API_VERSION + endpoint.getURL())
                        .then()
                        .assertThat()
                        .spec(responseSpecification)
                        .extract()
                        .body()
                        .as((Class<Res>) endpoint.getResponseModel()));

    }

    @Override
    public Res patch(long id, Rec model) {
        return StepLogger.log("PATCH request to " + endpoint.getURL(), () ->
                given()
                        .spec(requestSpecification)
                        .patch(API_VERSION + endpoint.getURL() + id)
                        .then()
                        .assertThat()
                        .spec(responseSpecification)
                        .extract()
                        .body()
                        .as((Class<Res>) endpoint.getResponseModel()));

    }

    @Override
    public Res delete(long id) {
        return StepLogger.log("DELETE request to " + endpoint.getURL(), () ->
                given()
                        .spec(requestSpecification)
                        .delete(API_VERSION + endpoint.getURL() + id)
                        .then()
                        .assertThat()
                        .spec(responseSpecification)
                        .extract()
                        .body()
                        .as((Class<Res>) endpoint.getResponseModel()));

    }

    public <T> List<T> getAsList(Class<T> elementType) {
        return StepLogger.log("GET request to " + endpoint.getURL(), () ->
                given()
                        .spec(requestSpecification)
                        .get(API_VERSION + endpoint.getURL())
                        .then()
                        .assertThat()
                        .spec(responseSpecification)
                        .extract()
                        .body()
                        .jsonPath()
                        .getList("$", elementType));
    }
}
