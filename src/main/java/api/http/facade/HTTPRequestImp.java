package api.http.facade;

import static io.restassured.RestAssured.given;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.util.List;

public class HTTPRequestImp<Rec, Res> extends AbstractHTTPRequest implements HTTPRequest<Rec, Res> {

  public HTTPRequestImp(
      RequestSpecification requestSpecification,
      Endpoint endpoint,
      ResponseSpecification responseSpecification) {
    super(requestSpecification, endpoint, responseSpecification);
  }

  @Override
  public Res post(Rec model) {
    return given()
        .spec(requestSpecification)
        .body(model)
        .post(endpoint.getURL())
        .then()
        .assertThat()
        .spec(responseSpecification)
        .extract()
        .body()
        .as((Class<Res>) endpoint.getResponseModel());
  }

  @Override
  public Res get() {
    return given()
        .spec(requestSpecification)
        .get(endpoint.getURL())
        .then()
        .assertThat()
        .spec(responseSpecification)
        .extract()
        .body()
        .as((Class<Res>) endpoint.getResponseModel());
  }

  @Override
  public Res put(Rec model) {
    return given()
        .spec(requestSpecification)
        .body(model)
        .put(endpoint.getURL())
        .then()
        .assertThat()
        .spec(responseSpecification)
        .extract()
        .body()
        .as((Class<Res>) endpoint.getResponseModel());
  }

  @Override
  public Res patch(long id, Rec model) {
    return given()
        .spec(requestSpecification)
        .patch(endpoint.getURL() + id)
        .then()
        .assertThat()
        .spec(responseSpecification)
        .extract()
        .body()
        .as((Class<Res>) endpoint.getResponseModel());
  }

  @Override
  public Res delete(long id) {
    return given()
        .spec(requestSpecification)
        .delete(endpoint.getURL() + id)
        .then()
        .assertThat()
        .spec(responseSpecification)
        .extract()
        .body()
        .as((Class<Res>) endpoint.getResponseModel());
  }

  public <T> List<T> getAsList(Class<T> elementType) {
    return given()
        .spec(requestSpecification)
        .get(endpoint.getURL())
        .then()
        .assertThat()
        .spec(responseSpecification)
        .extract()
        .body()
        .jsonPath()
        .getList("$", elementType);
  }
}
