package api.http.facade;

import static io.restassured.RestAssured.given;

import api.configs.AppConfig;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class ValidatedHTTPRequestImp<Rec, Res> extends AbstractHTTPRequest
    implements HTTPRequest<Rec, Res> {
  private static final String API_VERSION = AppConfig.getProperty("apiVersion");

  public ValidatedHTTPRequestImp(
      RequestSpecification requestSpecification,
      Endpoint endpoint,
      ResponseSpecification responseSpecification) {
    super(requestSpecification, endpoint, responseSpecification);
  }

  @Override
  public Res post(Rec model) {
    return (Res)
        given()
            .spec(requestSpecification)
            .body(model)
            .post(API_VERSION + endpoint.getURL())
            .then()
            .assertThat()
            .spec(responseSpecification);
  }

  @Override
  public Res get() {
    return (Res)
        given()
            .spec(requestSpecification)
            .get(API_VERSION + endpoint.getURL())
            .then()
            .assertThat()
            .spec(responseSpecification);
  }

  @Override
  public Res put(Rec model) {
    return (Res)
        given()
            .spec(requestSpecification)
            .body(model)
            .put(API_VERSION + endpoint.getURL())
            .then()
            .assertThat()
            .spec(responseSpecification);
  }

  @Override
  public Res patch(long id, Rec model) {
    return (Res)
        given()
            .spec(requestSpecification)
            .body(model)
            .patch(API_VERSION + endpoint.getURL() + id)
            .then()
            .assertThat()
            .spec(responseSpecification);
  }

  @Override
  public Res delete(long id) {
    return (Res)
        given()
            .spec(requestSpecification)
            .delete(API_VERSION + endpoint.getURL() + id)
            .then()
            .assertThat()
            .spec(responseSpecification);
  }
}
