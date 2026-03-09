package api.http.facade;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class ValidatedHTTPRequestImp<Rec, Res> extends AbstractHTTPRequest implements HTTPRequest<Rec, Res> {
    public ValidatedHTTPRequestImp(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public Res post (Rec model) {
        return (Res) given()
                .spec(requestSpecification)
                .body(model)
                .post(endpoint.getURL())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public Res get() {
        return (Res) given()
                .spec(requestSpecification)
                .get(endpoint.getURL())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public Res put(Rec model) {
        return (Res) given()
                .spec(requestSpecification)
                .body(model)
                .put(endpoint.getURL())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public Res patch(long id, Rec model) {
        return (Res) given()
                .spec(requestSpecification)
                .body(model)
                .patch(endpoint.getURL() + id)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public Res delete(long id) {
        return (Res) given()
                .spec(requestSpecification)
                .delete(endpoint.getURL() + id)
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
