package api.steps.accounts;

import api.dto.accounts.*;
import api.dto.admin.CreateUserRequest;
import api.http.facade.Endpoint;
import api.http.facade.HTTPRequestImp;
import api.http.facade.ValidatedHTTPRequestImp;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.steps.BaseSteps;
import io.restassured.response.ValidatableResponse;

public class AccountsStepsByAuth extends BaseSteps implements AccountsSteps {
  private final String username;
  private final String password;

  public AccountsStepsByAuth(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public AccountResponse createAccount() {
    CreateUserRequest createUserRequest = generateTestUser();
    return new HTTPRequestImp<Object, AccountResponse>(
            RequestSpecs.authAsUser(username, password),
            Endpoint.CREATE_ACCOUNTS,
            ResponseSpecs.entityWasCreated())
        .post(createUserRequest);
  }

  @Override
  public AccountResponse depositMoney(long id, double amount) {
    return new HTTPRequestImp<DepositMoneyRequest, AccountResponse>(
            RequestSpecs.authAsUser(username, password),
            Endpoint.DEPOSIT_MONEY,
            ResponseSpecs.requestReturnsOK())
        .post(new DepositMoneyRequest(id, amount));
  }

  @Override
  public ValidatableResponse depositMoney(long id, double amount, int errorCode) {
    return new ValidatedHTTPRequestImp<DepositMoneyRequest, ValidatableResponse>(
            RequestSpecs.authAsUser(username, password),
            Endpoint.DEPOSIT_MONEY,
            ResponseSpecs.requestReturnsError(errorCode))
        .post(new DepositMoneyRequest(id, amount));
  }

  @Override
  public ValidatableResponse depositMoneyAuthByToken(long id, double amount, int errorCode) {
    throw new UnsupportedOperationException("Use AccountsStepsByToken for token-based operations");
  }

  @Override
  public TransferMoneyResponse transferMoney(
      long senderAccountId, long receiverAccountId, double amount) {
    var model = new TransferMoneyRequest(senderAccountId, receiverAccountId, amount);
    return new HTTPRequestImp<TransferMoneyRequest, TransferMoneyResponse>(
            RequestSpecs.authAsUser(username, password),
            Endpoint.TRANSFER_MONEY,
            ResponseSpecs.requestReturnsOK())
        .post(model);
  }

  @Override
  public ValidatableResponse transferMoney(
      long senderAccountId, long receiverAccountId, double amount, int errorCode) {
    var model = new TransferMoneyRequest(senderAccountId, receiverAccountId, amount);
    return new ValidatedHTTPRequestImp<TransferMoneyRequest, ValidatableResponse>(
            RequestSpecs.authAsUser(username, password),
            Endpoint.TRANSFER_MONEY,
            ResponseSpecs.requestReturnsError(errorCode))
        .post(model);
  }

  @Override
  public ValidatableResponse transferMoneyAuthByToken(
      long senderAccountId, long receiverAccountId, double amount, int errorCode) {
    throw new UnsupportedOperationException("Use AccountsStepsByToken for token-based operations");
  }

  @Override
  public TransferResponse transferWithFraudCheck(
      Long senderAccountId, Long receiverAccountId, double amount) {

    TransferRequest transferRequest =
        TransferRequest.builder()
            .senderAccountId(senderAccountId)
            .receiverAccountId(receiverAccountId)
            .amount(amount)
            .description("Test transfer with fraud check")
            .build();

    return new HTTPRequestImp<TransferRequest, TransferResponse>(
            RequestSpecs.authAsUser(username, password),
            Endpoint.TRANSFER_WITH_FRAUD_CHECK,
            ResponseSpecs.requestReturnsOK())
        .post(transferRequest);
  }
}
