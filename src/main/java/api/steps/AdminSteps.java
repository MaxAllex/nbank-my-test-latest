package api.steps;

import api.dto.TestUser;
import api.dto.admin.CreateUserRequest;
import api.dto.admin.CreateUserResponse;
import api.http.facade.Endpoint;
import api.http.facade.HTTPRequestImp;
import api.http.facade.ValidatedHTTPRequestImp;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helpers.StepLogger;
import java.util.List;
import java.util.Map;

public class AdminSteps extends BaseSteps {
  public TestUser createUser(Map<String, Object> overrides) {
    return StepLogger.log(
        "Create user ",
        () -> {
          CreateUserRequest userRequest = generateTestUser(overrides);
          CreateUserResponse userResponse =
              new HTTPRequestImp<CreateUserRequest, CreateUserResponse>(
                      RequestSpecs.adminSpec(),
                      Endpoint.CREATE_USER,
                      ResponseSpecs.entityWasCreated())
                  .post(userRequest);
          return new TestUser(userRequest, userResponse, null);
        });
  }

  public void deleteUser(long userId) {
    StepLogger.log(
        "Delete users ",
        () -> {
          new ValidatedHTTPRequestImp<>(
                  RequestSpecs.adminSpec(), Endpoint.DELETE_USER, ResponseSpecs.requestReturnsOK())
              .delete(userId);
        });
  }

  public List<CreateUserResponse> getUsers() {
    return StepLogger.log(
        "Get users ",
        () -> {
          return new HTTPRequestImp<Object, CreateUserResponse>(
                  RequestSpecs.adminSpec(),
                  Endpoint.GET_ALL_USERS,
                  ResponseSpecs.requestReturnsOK())
              .getAsList(CreateUserResponse.class);
        });
  }
}
