package api.steps.customer;

import api.dto.customer.GetCustomerAccountResponse;
import api.dto.customer.GetCustomerProfileResponse;
import io.restassured.response.ValidatableResponse;

import java.util.List;

public interface CustomerSteps {
    GetCustomerProfileResponse getCustomerProfile();
    List<GetCustomerAccountResponse> getCustomerAccounts();
    ValidatableResponse updateCustomerProfile(String name, int errorCode);
    ValidatableResponse updateCustomerProfile(String name);
}
