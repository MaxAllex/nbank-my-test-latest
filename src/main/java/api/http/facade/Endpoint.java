package api.http.facade;

import api.dto.accounts.*;
import api.dto.admin.CreateUserRequest;
import api.dto.admin.CreateUserResponse;
import api.dto.admin.GetAllUsersResponse;
import api.dto.authentication.LoginRequest;
import api.dto.authentication.LoginResponse;
import api.dto.customer.GetCustomerAccountResponse;
import api.dto.customer.GetCustomerProfileResponse;
import api.dto.customer.UpdateCustomerProfileRequest;
import api.dto.customer.UpdateCustomerProfileResponse;
import lombok.Getter;

@Getter
public enum Endpoint {
  GET_ALL_USERS("/admin/users", null, GetAllUsersResponse.class),
  CREATE_USER("/admin/users", CreateUserRequest.class, CreateUserResponse.class),
  DELETE_USER("/admin/users/", CreateUserRequest.class, CreateUserResponse.class),
  GET_CUSTOMER_PROFILE("/customer/profile", null, GetCustomerProfileResponse.class),
  GET_CUSTOMER_ACCOUNT("/customer/accounts", null, GetCustomerAccountResponse.class),
  UPDATE_CUSTOMER_PROFILE(
      "/customer/profile", UpdateCustomerProfileRequest.class, UpdateCustomerProfileResponse.class),
  LOGIN("/auth/login", LoginRequest.class, LoginResponse.class),
  CREATE_ACCOUNTS("/accounts", null, AccountResponse.class),
  DEPOSIT_MONEY("/accounts/deposit", null, AccountResponse.class),
  TRANSFER_MONEY("/accounts/transfer", TransferMoneyRequest.class, TransferMoneyResponse.class),
  TRANSFER_WITH_FRAUD_CHECK(
      "/accounts/transfer-with-fraud-check", TransferRequest.class, TransferResponse.class);
  private final String URL;
  private final Class<?> requestModel;
  private final Class<?> responseModel;

  Endpoint(String URL, Class<?> requestModel, Class<?> responseModel) {
    this.URL = URL;
    this.requestModel = requestModel;
    this.responseModel = responseModel;
  }
}
