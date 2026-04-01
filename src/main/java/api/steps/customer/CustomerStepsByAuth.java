package api.steps.customer;

import api.dto.customer.GetCustomerAccountResponse;
import api.dto.customer.GetCustomerProfileResponse;
import api.dto.customer.UpdateCustomerProfileRequest;
import api.http.facade.Endpoint;
import api.http.facade.HTTPRequestImp;
import api.http.facade.ValidatedHTTPRequestImp;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helpers.StepLogger;
import io.restassured.response.ValidatableResponse;
import java.util.List;

public class CustomerStepsByAuth implements CustomerSteps {
  private final String username;
  private final String password;

  public CustomerStepsByAuth(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public GetCustomerProfileResponse getCustomerProfile() {
    return StepLogger.log("Get Customer Profile " , () -> {
    return new HTTPRequestImp<Object, GetCustomerProfileResponse>(
            RequestSpecs.authAsUser(username, password),
            Endpoint.GET_CUSTOMER_PROFILE,
            ResponseSpecs.requestReturnsOK())
        .get();
    });
  }

  @Override
  public List<GetCustomerAccountResponse> getCustomerAccounts() {
    return StepLogger.log("Get Customer Accounts " , () -> {
    return new HTTPRequestImp<Object, GetCustomerAccountResponse>(
            RequestSpecs.authAsUser(username, password),
            Endpoint.GET_CUSTOMER_ACCOUNT,
            ResponseSpecs.requestReturnsOK())
        .getAsList(GetCustomerAccountResponse.class);
    });
  }

  @Override
  public ValidatableResponse updateCustomerProfile(String name, int errorCode) {
    return StepLogger.log("Update Customer Profile " , () -> {
    return new ValidatedHTTPRequestImp<UpdateCustomerProfileRequest, ValidatableResponse>(
            RequestSpecs.authAsUser(username, password),
            Endpoint.UPDATE_CUSTOMER_PROFILE,
            ResponseSpecs.requestReturnsError(errorCode))
        .put(new UpdateCustomerProfileRequest(name));
    });
  }

  @Override
  public ValidatableResponse updateCustomerProfile(String name) {
    return StepLogger.log("Update Customer Profile " , () -> {
    return new ValidatedHTTPRequestImp<UpdateCustomerProfileRequest, ValidatableResponse>(
            RequestSpecs.authAsUser(username, password),
            Endpoint.UPDATE_CUSTOMER_PROFILE,
            ResponseSpecs.requestReturnsOK())
        .put(new UpdateCustomerProfileRequest(name));
    });
  }
}
