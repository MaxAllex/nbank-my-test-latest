package api.steps.accounts;

import api.dto.accounts.*;
import api.http.facade.Endpoint;
import api.http.facade.ValidatedHTTPRequestImp;
import io.restassured.response.ValidatableResponse;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.steps.BaseSteps;

public class AccountsStepsByToken extends BaseSteps implements AccountsSteps {
    private final String token;

    public AccountsStepsByToken(String token) {
        this.token = token;
    }

    @Override
    public AccountResponse createAccount() {
        throw new UnsupportedOperationException("Use AccountsStepsByAuth for credential-based operations");
    }

    @Override
    public AccountResponse depositMoney(long id, double amount) {
        throw new UnsupportedOperationException("Use AccountsStepsByAuth for credential-based operations");
    }

    @Override
    public ValidatableResponse depositMoney(long id, double amount, int errorCode) {
        throw new UnsupportedOperationException("Use AccountsStepsByAuth for credential-based operations");
    }

    @Override
    public ValidatableResponse depositMoneyAuthByToken(long id, double amount, int errorCode) {
        return new ValidatedHTTPRequestImp<DepositMoneyRequest, ValidatableResponse>(
                RequestSpecs.authByToken(token),
                Endpoint.DEPOSIT_MONEY,
                ResponseSpecs.requestReturnsError(errorCode)
        ).post(new DepositMoneyRequest(id, amount));
    }

    @Override
    public TransferMoneyResponse transferMoney(long senderAccountId, long receiverAccountId, double amount) {
        throw new UnsupportedOperationException("Use AccountsStepsByAuth for credential-based operations");
    }

    @Override
    public ValidatableResponse transferMoney(long senderAccountId, long receiverAccountId, double amount, int errorCode) {
        throw new UnsupportedOperationException("Use AccountsStepsByAuth for credential-based operations");
    }

    @Override
    public ValidatableResponse transferMoneyAuthByToken(long senderAccountId, long receiverAccountId, double amount, int errorCode) {
        var model = new TransferMoneyRequest(senderAccountId, receiverAccountId, amount);
        return new ValidatedHTTPRequestImp<TransferMoneyRequest, ValidatableResponse>(
                RequestSpecs.authByToken(token),
                Endpoint.TRANSFER_MONEY,
                ResponseSpecs.requestReturnsError(errorCode)
        ).post(model);
    }

    @Override
    public TransferResponse transferWithFraudCheck(Long senderAccountId, Long receiverAccountId, double amount) {
        return null;
    }
}
