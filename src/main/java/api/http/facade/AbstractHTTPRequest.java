package api.http.facade;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public abstract class AbstractHTTPRequest {
  protected RequestSpecification requestSpecification;
  protected Endpoint endpoint;
  protected ResponseSpecification responseSpecification;

  protected AbstractHTTPRequest(
      RequestSpecification requestSpecification,
      Endpoint endpoint,
      ResponseSpecification responseSpecification) {
    this.requestSpecification = requestSpecification;
    this.endpoint = endpoint;
    this.responseSpecification = responseSpecification;
  }

  static {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper
        .getFactory()
        .setStreamReadConstraints(
            StreamReadConstraints.builder().maxNestingDepth(Integer.MAX_VALUE).build());

    RestAssured.config =
        RestAssured.config()
            .objectMapperConfig(
                io.restassured.config.ObjectMapperConfig.objectMapperConfig()
                    .jackson2ObjectMapperFactory((cls, charset) -> mapper));
  }
}
