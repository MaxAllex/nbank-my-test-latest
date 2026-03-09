package api.steps.customer;

import api.http.facade.Endpoint;
import api.http.facade.HTTPRequestImp;
import api.http.facade.ValidatedHTTPRequestImp;
import api.dto.customer.GetCustomerAccountResponse;
import api.dto.customer.GetCustomerProfileResponse;
import api.dto.customer.UpdateCustomerProfileRequest;
import io.restassured.response.ValidatableResponse;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

public class CustomerStepsByToken implements CustomerSteps {
    private final String token;

    public CustomerStepsByToken(String token) {
        this.token = token;
    }

    @Override
    public GetCustomerProfileResponse getCustomerProfile() {
        return new HTTPRequestImp<Object, GetCustomerProfileResponse>(
                RequestSpecs.authByToken(token),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get();
    }

    @Override
    public List<GetCustomerAccountResponse> getCustomerAccounts() {
        return new HTTPRequestImp<Object, GetCustomerAccountResponse>(
                RequestSpecs.authByToken(token),
                Endpoint.GET_CUSTOMER_ACCOUNT,
                ResponseSpecs.requestReturnsOK())
                .getAsList(GetCustomerAccountResponse.class);
    }

    @Override
    public ValidatableResponse updateCustomerProfile(String name, int errorCode) {
        return new ValidatedHTTPRequestImp<UpdateCustomerProfileRequest, ValidatableResponse>(
                RequestSpecs.authByToken(token),
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsError(errorCode))
                .put(new UpdateCustomerProfileRequest(name));
    }

    @Override
    public ValidatableResponse updateCustomerProfile(String name) {
        return new ValidatedHTTPRequestImp<UpdateCustomerProfileRequest, ValidatableResponse>(
                RequestSpecs.authByToken(token),
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.entityWasCreated())
                .put(new UpdateCustomerProfileRequest(name));
    }
}
